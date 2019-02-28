package org.taymyr.lagom.elasticsearch.search.dsl;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.search.dsl.SuggestResult.SuggestOption;
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.CompletionSuggest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;

import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class SearchRequestTest {

    @Test
    @DisplayName("successfully serialize search request with wildcard query")
    void shouldSuccessfullySerializeTerm() {
        SearchRequest request = SearchRequest.builder()
            .query(IdsQuery.of("1"))
            .aggs(ImmutableMap.of("agg1", TermsAggregation.builder().field("field").build()))
            .sort(Order.desc("name"), Order.asc("age"))
            .suggest(ImmutableMap.of("suggest1", CompletionSuggest.builder()
                .prefix("prefix")
                .field("suggest")
                .build())
            )
            .postFilter(TermQuery.of("field", "value"))
            .from(0)
            .size(10)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with wildcard query with list")
    void shouldSuccessfullySerializeTermWithList() {
        List<Order> orderList = asList(Order.desc("name"), Order.asc("age"));
        List<String> ids = asList("1");
        SearchRequest request = SearchRequest.builder()
            .query(IdsQuery.of(ids))
            .aggs(ImmutableMap.of("agg1", TermsAggregation.builder().field("field").build()))
            .sort(orderList)
            .suggest(ImmutableMap.of("suggest1", CompletionSuggest.builder()
                .prefix("prefix")
                .field("suggest")
                .build())
            )
            .postFilter(TermQuery.of("field", "value"))
            .from(0)
            .size(10)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully deserialize search result")
    void shouldSuccessfullyDeserializeSearchResult() {
        TestDocumentResult result = deserializeResponse(
            resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/result.json"),
            TestDocumentResult.class
        );
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getTook()).isEqualTo(2);
        assertThat(result.getHits().getTotal()).isEqualTo(1);
        assertThat(result.getHits().getHits()).hasSize(1);
        HitResult<TestDocument> hitResult = result.getHits().getHits().get(0);
        assertThat(hitResult.getScore()).isEqualTo(1.3862944);
        TestDocument source = result.getSources().get(0);
        assertThat(source.getUser()).isEqualTo("kimchy");
        assertThat(source.getMessage()).isEqualTo("trying out Elasticsearch");
        assertThat(result.getSuggest()).containsKeys("my-suggest-1");
        List<SuggestResult<TestDocument>> suggestResults = result.getSuggest().get("my-suggest-1");
        SuggestResult<TestDocument> suggest1 = new SuggestResult<>("tring", singletonList(new SuggestOption<>("trying")));
        SuggestResult<TestDocument> suggest2 = new SuggestResult<>("out", emptyList());
        SuggestResult<TestDocument> suggest3 = new SuggestResult<>("elasticsearch", emptyList());
        assertThat(suggestResults).containsExactly(suggest1, suggest2, suggest3);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> SearchRequest.builder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Query can't be null");
    }
}