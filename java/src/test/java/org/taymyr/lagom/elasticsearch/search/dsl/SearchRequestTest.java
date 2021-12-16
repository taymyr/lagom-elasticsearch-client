package org.taymyr.lagom.elasticsearch.search.dsl;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.script.Script;
import org.taymyr.lagom.elasticsearch.search.dsl.SuggestResult.SuggestOption;
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.script.ScriptQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.CompletionSuggest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;
import static org.taymyr.lagom.elasticsearch.search.dsl.query.Query.MATCH_ALL;

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
            .minScore(0.3)
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
            .minScore(0.3)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with script fields")
    void shouldSuccessfullySerializeTermWithScriptFields() {
        SearchRequest request = SearchRequest.builder()
            .query(MATCH_ALL)
            .scriptField("field1", Script.builder()
                .id("script1")
                .param("param1", "value1")
                .build()
            )
            .scriptField("field2", Script.builder()
                .source("select")
                .param("param2", 2.5)
                .lang("painless")
                .build()
            )
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/request_script_fields.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with script filters")
    void shouldSuccessfullySerializeTermWithScriptFilter() {
        SearchRequest request = SearchRequest.builder()
            .query(BoolQuery.builder()
                .must(MATCH_ALL)
                .must(new ScriptQuery(Script.builder().id("script1").param("param1", "value1").build()))
                .must(new ScriptQuery(Script.builder().source("select").param("param2", 2.5).lang("painless").build()))
                .build())
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/request_script_filter.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with search after parameter")
    void shouldSuccessfullySerializeTermWithSearchAfter() {
        List<String> ids = asList("1");
        SearchRequest request = SearchRequest.builder()
            .query(IdsQuery.of(ids))
            .aggs(ImmutableMap.of("agg1", TermsAggregation.builder().field("field").build()))
            .sort(Order.desc("name"), Order.asc("age"))
            .suggest(ImmutableMap.of("suggest1", CompletionSuggest.builder()
                .prefix("prefix")
                .field("suggest")
                .build())
            )
            .postFilter(TermQuery.of("field", "value"))
            .minScore(0.3)
            .searchAfter("John", 18)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/request_search_after.json");
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
        assertThat(result.getHits().getTotal().getValue()).isEqualTo(1);
        assertThat(result.getHits().getHits()).hasSize(1);
        HitResult<TestDocument> hitResult = result.getHits().getHits().get(0);
        assertThat(hitResult.getScore()).isEqualTo(1.3862944);
        assertThat(hitResult.getHighlight()).isNotNull();
        assertThat(hitResult.getHighlight().get("message")).containsExactly("<em>trying</em> out Elasticsearch", "trying out <em>Elasticsearch</em>");
        assertThat(hitResult.getHighlight().get("user")).containsExactly("<em>kimchy</em>");
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

    @Test
    @DisplayName("source filter serialization")
    void shouldSerializeSourceFilter() {
        SearchRequest request = SearchRequest.builder()
            .source(SourceFilter.EXCLUDE_SOURCE)
            .query(IdsQuery.of("1"))
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/source/source_excluded.json");
        assertThatJson(actual).isEqualTo(expected);

        request = SearchRequest.builder()
            .source(SourceFilter.INCLUDE_SOURCE)
            .query(IdsQuery.of("1"))
            .build();
        actual = serializeRequest(request, SearchRequest.class);
        expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/source/source_included.json");
        assertThatJson(actual).isEqualTo(expected);

        request = SearchRequest.builder()
            .source(SourceFilter.singlePath("order.client.*"))
            .query(IdsQuery.of("1"))
            .build();
        actual = serializeRequest(request, SearchRequest.class);
        expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/source/source_single_path.json");
        assertThatJson(actual).isEqualTo(expected);

        request = SearchRequest.builder()
            .source(SourceFilter.multiPath(asList("order.client.*", "order.price.*")))
            .query(IdsQuery.of("1"))
            .build();
        actual = serializeRequest(request, SearchRequest.class);
        expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/source/source_multi_path.json");
        assertThatJson(actual).isEqualTo(expected);

        request = SearchRequest.builder()
            .source(SourceFilter.multiPath(asList("order.client.*", "order.seller.*"), asList( "order.price.*")))
            .query(IdsQuery.of("1"))
            .build();
        actual = serializeRequest(request, SearchRequest.class);
        expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/source/source_includes_excludes.json");
        assertThatJson(actual).isEqualTo(expected);
    }
}
