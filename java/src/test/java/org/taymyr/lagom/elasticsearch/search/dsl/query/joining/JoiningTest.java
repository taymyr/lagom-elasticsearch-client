package org.taymyr.lagom.elasticsearch.search.dsl.query.joining;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class JoiningTest {

    @Test
    @DisplayName("successfully serialize search request with nested query")
    void shouldSuccessfullySerializeNestedQuery() {
        Match match = new Match() {
            @JsonProperty("obj1.name")
            private String name = "blue";
        };
        SearchRequest request = SearchRequest.builder()
                .query(NestedQuery.builder()
                        .path("obj1")
                        .scoreMode(NestedQueryScoreMode.AVG)
                        .query(BoolQuery.builder().must(MatchQuery.of(match)).build())
                        .build()
                ).build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/joining/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> NestedQuery.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'path' can't be null");
        assertThatThrownBy(() -> NestedQuery.builder().path("path").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'query' can't be null");
    }
}
