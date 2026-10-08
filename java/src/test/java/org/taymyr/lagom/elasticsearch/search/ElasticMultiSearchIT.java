package org.taymyr.lagom.elasticsearch.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchItem;
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchResult;
import org.taymyr.lagom.elasticsearch.search.dsl.query.MatchAllQuery;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.ServiceCall.invoke;

class ElasticMultiSearchIT extends AbstractElasticsearchIT {

    private final SearchRequest matchAll = SearchRequest.builder().query(MatchAllQuery.of()).build();

    @Test
    @DisplayName("Multi search should return results of every search in order of the request")
    void shouldReturnResultsInOrderOfRequest() throws Exception {
        index("msearch-test-1", "1", new TestDocument("user1", "message1"));
        index("msearch-test-1", "2", new TestDocument("user2", "message2"));
        index("msearch-test-2", "1", new TestDocument("user3", "message3"));
        sleep(1000);

        MultiSearchResult result = eventually(elasticSearch.msearch().invoke(MultiSearchRequest.of(
                MultiSearchItem.of("msearch-test-1", matchAll),
                MultiSearchItem.of("msearch-test-2", matchAll),
                MultiSearchItem.of("msearch-test-missing", matchAll)
        )));

        assertThat(result.getResponses()).hasSize(3);

        TestDocumentResult first = result.result(0, TestDocumentResult.class);
        assertThat(first.getHits().getTotal().getValue()).isEqualTo(2);

        TestDocumentResult second = result.result(1, TestDocumentResult.class);
        assertThat(second.getSources()).containsExactly(new TestDocument("user3", "message3"));

        assertThat(result.isError(2)).isTrue();
        assertThat(result.status(2)).isEqualTo(404);
    }

    @Test
    @DisplayName("Multi search should use the index from the path for searches without own index")
    void shouldUseIndexFromPath() throws Exception {
        index("msearch-test-default", "1", new TestDocument("user1", "message1"));
        sleep(1000);

        MultiSearchResult result = eventually(elasticSearch.msearch("msearch-test-default").invoke(MultiSearchRequest.of(
                new MultiSearchItem(matchAll),
                new MultiSearchItem(matchAll)
        )));

        assertThat(result.getResponses()).hasSize(2);
        for (int i = 0; i < 2; i++) {
            assertThat(result.isError(i)).isFalse();
            assertThat(result.result(i, TestDocumentResult.class).getSources())
                    .containsExactly(new TestDocument("user1", "message1"));
        }
    }

    private void index(String index, String id, TestDocument document) throws Exception {
        eventually(invoke(elasticDocument.indexWithId(index, id), document));
    }
}
