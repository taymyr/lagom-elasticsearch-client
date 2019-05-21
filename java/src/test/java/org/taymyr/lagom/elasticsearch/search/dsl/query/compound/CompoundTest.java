package org.taymyr.lagom.elasticsearch.search.dsl.query.compound;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class CompoundTest {

    @Test
    @DisplayName("successfully serialize search request with must bool query")
    void shouldSuccessfullySerializeMustBoolQuery() {
        SearchRequest request = SearchRequest.builder()
            .query(BoolQuery.builder().must(IdsQuery.of("1")).build())
            .size(9999)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/compound/request_must.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with must not bool query")
    void shouldSuccessfullySerializeMustNotBoolQuery() {
        SearchRequest request = SearchRequest.builder()
            .query(BoolQuery.builder().mustNot(IdsQuery.of("1")).build())
            .size(9999)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/compound/request_must_not.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with should bool query")
    void shouldSuccessfullySerializeShouldBoolQuery() {
        SearchRequest request = SearchRequest.builder()
            .query(BoolQuery.builder().should(IdsQuery.of("1")).build())
            .size(9999)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/compound/request_should.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with filter bool query")
    void shouldSuccessfullySerializeFilteredBoolQuery() {
        SearchRequest request = SearchRequest.builder()
            .query(BoolQuery.builder().filter(IdsQuery.of("1")).build())
            .size(9999)
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/compound/request_filter.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with list of conditions")
    void shouldSuccessfullySerializeMustBoolQueryWithList() {
        SearchRequest request = SearchRequest.builder()
                .query(BoolQuery.builder()
                        .filter(ImmutableList.of(IdsQuery.of("1"), TermQuery.of("names", "name1")))
                        .must(ImmutableList.of(IdsQuery.of("1"), TermQuery.of("names", "name1")))
                        .mustNot(ImmutableList.of(IdsQuery.of("1"), TermQuery.of("names", "name1")))
                        .should(ImmutableList.of(IdsQuery.of("1"), TermQuery.of("names", "name1")))
                        .build())
                .size(9999)
                .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/compound/request_condition_list.json");
        assertThatJson(actual).isEqualTo(expected);
    }

}
