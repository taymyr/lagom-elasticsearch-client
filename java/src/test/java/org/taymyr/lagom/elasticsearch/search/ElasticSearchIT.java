package org.taymyr.lagom.elasticsearch.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.document.dsl.index.IndexResult;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.ServiceCall.invoke;

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
        assertThat(result.getHits().getTotal()).isEqualTo(1);
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
    }

}