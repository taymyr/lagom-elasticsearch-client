package org.taymyr.lagom.elasticsearch.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.pcollections.HashTreePMap;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.document.dsl.index.IndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.ExistsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.taymyr.lagom.elasticsearch.ServiceCall.invoke;
import static org.taymyr.lagom.elasticsearch.indices.dsl.DataType.KEYWORD;
import static org.taymyr.lagom.elasticsearch.indices.dsl.DataType.TEXT;
import static org.taymyr.lagom.elasticsearch.search.dsl.query.Order.asc;

import static java.lang.Thread.sleep;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;

class ElasticSearchIT extends AbstractElasticsearchIT {

    @Test
    @DisplayName("Search service descriptor should work correct")
    void shouldWorkCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createDocument();
        sleep(1000);
        searchById();
    }

    void createDocument() throws InterruptedException, ExecutionException, TimeoutException {
        IndexResult result = eventually(invoke(elasticDocument.indexWithId("test", "sample", "1"),
                new TestDocument("user", "message")));
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
    }

    void check(TestDocumentResult result) {
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getHits().getTotal().getValue()).isEqualTo(1);
        assertThat(result.getHits().getHits()).hasSize(1);
        assertThat(result.getHits().getHits().get(0).getScore()).isEqualTo(1.0);
        assertThat(result.getHits().getHits().get(0).getSource().getUser()).isEqualTo("user");
        assertThat(result.getHits().getHits().get(0).getSource().getMessage()).isEqualTo("message");
    }

    void searchById() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest(IdsQuery.of("1"));
        check(eventually(invoke(elasticSearch.search(asList("test"), asList("sample")), searchRequest, TestDocumentResult.class)));
        check(eventually(invoke(elasticSearch.search(asList("test")), searchRequest, TestDocumentResult.class)));
        check(eventually(invoke(elasticSearch.search("test", "sample"), searchRequest, TestDocumentResult.class)));
        check(eventually(invoke(elasticSearch.search("test"), searchRequest, TestDocumentResult.class)));
        assertThat(eventually(elasticSearch.count("test").invoke(searchRequest)).getCount()).isEqualTo(1);
    }

    @Test
    void testSearchScroller() throws InterruptedException, TimeoutException, ExecutionException {
        String indexName = "search-after-idx";
        String type = "_doc";
        elasticIndices.create(indexName).invoke(new CreateIndex(
            new Settings(1, 1),
            new Mapping(
                HashTreePMap.<String, MappingProperty>empty()
                    .plus("user", MappingProperty.builder().type(KEYWORD).build())
                    .plus("message", MappingProperty.builder().type(TEXT).build())
            )
        ));
        sleep(Duration.of(5, SECONDS).toMillis());
        int firstId = 1;
        int latestId = 12000;
        IntStream.range(firstId, latestId + 1).forEach(i -> {
            try {
                eventually(invoke(elasticDocument.indexWithId(indexName, type, String.valueOf(i)),
                    new TestDocument("user" + i, "message" + i)));
            } catch (Throwable e) {
                throw  new RuntimeException(e);
            }
        });
        sleep(Duration.of(5, SECONDS).toMillis());
        SearchRequest searchRequest = SearchRequest.builder()
            .query(ExistsQuery.of("user"))
            .sort(asc("user"))
            .build();
        List<TestDocument> cs = eventually(
            new SearchScroller(elasticSearch, indexName, type).searchAfter(searchRequest, TestDocumentResult.class),
            Duration.of(30, SECONDS)
        );
        assertThat(cs).isNotEmpty().hasSize(latestId)
            .extracting(TestDocument::getUser, TestDocument::getMessage)
            .contains(
                tuple("user" + latestId, "message" + latestId),
                tuple("user" + firstId, "message" + firstId)
            );
    }
}
