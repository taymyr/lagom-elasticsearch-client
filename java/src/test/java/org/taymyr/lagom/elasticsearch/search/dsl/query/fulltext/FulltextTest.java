package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;

import java.util.Arrays;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class FulltextTest {

    @Test
    @DisplayName("successfully serialize search request with multi-match")
    void shouldSuccessfullySerializeMultiMatch() {
        SearchRequest request = SearchRequest.builder()
                .query(MultiMatchQuery.builder()
                        .query("query")
                        .field("fullTextBoosted", 10)
                        .field("fullText")
                        .fields("field1", "field2")
                        .type(MultiMatchQueryType.CROSS_FIELDS)
                        .build()
                ).build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/multi_match.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with multi-match with list")
    void shouldSuccessfullySerializeMultiMatchWithList() {
        List<String> fields = Arrays.asList("field1", "field2");
        SearchRequest request = SearchRequest.builder()
            .query(MultiMatchQuery.builder()
                .query("query")
                .field("fullTextBoosted", 10)
                .field("fullText")
                .fields(fields)
                .type(MultiMatchQueryType.CROSS_FIELDS)
                .build()
            ).build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/multi_match.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with match phrase")
    void shouldSuccessfullySerializeMatchPhraseQuery() {
        MatchPhrase matchPhrase = new MatchPhrase() {
            @JsonProperty
            private String field = "value";
        };
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().filter(MatchPhraseQuery.of(matchPhrase)).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match_phrase.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with match phrase prefix")
    void shouldSuccessfullySerializeMatchPhrasePrefixQuery() {
        MatchPhrasePrefix matchPhrasePrefix = new MatchPhrasePrefix() {
            @JsonProperty
            private String field = "value";
        };
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().filter(MatchPhrasePrefixQuery.of(matchPhrasePrefix)).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match_phrase_prefix.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with match query")
    void shouldSuccessfullySerializeMatchQuery() {
        Match match = new Match() {
            @JsonProperty
            private String field = "value";
        };
        SearchRequest request = new SearchRequest(MatchQuery.of(match));
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> MultiMatchQuery.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'query' can't be null");
        assertThatThrownBy(() -> MultiMatchQuery.builder().query("query").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Fields can't be empty");
    }
}
