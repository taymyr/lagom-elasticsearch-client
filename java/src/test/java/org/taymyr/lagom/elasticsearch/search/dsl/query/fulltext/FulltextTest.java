package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;

import java.util.Arrays;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
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
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().filter(MatchPhraseQuery.builder()
                        .field("field")
                        .query("value")
                        .analyzer("analyzer")
                        .zeroTermsQuery(ZeroTerms.NONE)
                        .slop(1)
                        .build()).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match_phrase.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with match phrase prefix")
    void shouldSuccessfullySerializeMatchPhrasePrefixQuery() {
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().filter(MatchPhrasePrefixQuery.builder()
                        .field("field")
                        .query("value")
                        .analyzer("analyzer")
                        .zeroTermsQuery(ZeroTerms.NONE)
                        .slop(1)
                        .maxExpansions(10)
                        .build()).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match_phrase_prefix.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with match query")
    void shouldSuccessfullySerializeMatchQuery() {
        SearchRequest request = new SearchRequest(MatchQuery.of("field", "value"));
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with full match query")
    void shouldSuccessfullySerializeFullMatchQuery() {
        SearchRequest request = new SearchRequest(MatchQuery.builder()
                .field("field")
                .query("value")
                .operator(MatchOperator.OR)
                .minimumShouldMatch("2")
                .analyzer("analyzer")
                .lenient(true)
                .zeroTermsQuery(ZeroTerms.ALL)
                .cutoffFrequency(0.001)
                .autoGenerateSynonymsPhraseQuery(false)
                .build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/fulltext/match_all.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> MatchQuery.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field name can't be null");
        assertThatThrownBy(() -> MatchQuery.builder().field("field").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Query can't be null");
        assertThatThrownBy(() -> MultiMatchQuery.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'query' can't be null");
        assertThatThrownBy(() -> MultiMatchQuery.builder().query("query").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Fields can't be empty");
    }

    @Test
    @DisplayName("successfully build query with match phrase")
    void shouldSuccessfullyBuildMatchPhraseQuery() {
        MatchPhraseQuery actual = MatchPhraseQuery.builder()
                .field("testfield")
                .query("testvalue")
                .analyzer("testanalyzer")
                .zeroTermsQuery(ZeroTerms.ALL)
                .slop(10)
                .build();
        assertThat(actual.getField()).isEqualTo("testfield");
        assertThat(actual.getMatchPhrase()).isNotNull();
        assertThat(actual.getMatchPhrase().getQuery()).isEqualTo("testvalue");
        assertThat(actual.getMatchPhrase().getAnalyzer()).isEqualTo("testanalyzer");
        assertThat(actual.getMatchPhrase().getZeroTermsQuery()).isEqualTo(ZeroTerms.ALL);
        assertThat(actual.getMatchPhrase().getSlop()).isEqualTo(10);
    }

    @Test
    @DisplayName("successfully build query with match phrase (using method \"of\")")
    void shouldSuccessfullyBuildMatchPhraseQueryUsingOf() {
        MatchPhraseQuery actual = MatchPhraseQuery.of("testfield", "testvalue");
        assertThat(actual.getField()).isEqualTo("testfield");
        assertThat(actual.getMatchPhrase()).isNotNull();
        assertThat(actual.getMatchPhrase().getQuery()).isEqualTo("testvalue");
    }

    @Test
    @DisplayName("throw exception for incorrect builder invocation for query with match phrase")
    void shouldThrowExceptionForIncorrectBuilderMatchPhraseQuery() {
        assertThatThrownBy(() -> MatchPhraseQuery.builder().query("testvalue").slop(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field name can't be null");
        assertThatThrownBy(() -> MatchPhraseQuery.builder().field("testfield").slop(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Query can't be null");
    }

    @Test
    @DisplayName("successfully build query with match phrase prefix")
    void shouldSuccessfullyBuildMatchPhrasePrefixQuery() {
        MatchPhrasePrefixQuery actual = MatchPhrasePrefixQuery.builder()
                .field("testfield")
                .query("testvalue")
                .analyzer("testanalyzer")
                .zeroTermsQuery(ZeroTerms.ALL)
                .slop(10)
                .maxExpansions(1)
                .build();
        assertThat(actual.getField()).isEqualTo("testfield");
        assertThat(actual.getMatchPhrasePrefix()).isNotNull();
        assertThat(actual.getMatchPhrasePrefix().getQuery()).isEqualTo("testvalue");
        assertThat(actual.getMatchPhrasePrefix().getAnalyzer()).isEqualTo("testanalyzer");
        assertThat(actual.getMatchPhrasePrefix().getZeroTermsQuery()).isEqualTo(ZeroTerms.ALL);
        assertThat(actual.getMatchPhrasePrefix().getSlop()).isEqualTo(10);
        assertThat(actual.getMatchPhrasePrefix().getMaxExpansions()).isEqualTo(1);
    }

    @Test
    @DisplayName("successfully build query with match phrase prefix (using method \"of\")")
    void shouldSuccessfullyBuildMatchPhrasePrefixQueryUsingOf() {
        MatchPhrasePrefixQuery actual = MatchPhrasePrefixQuery.of("testfield", "testvalue");
        assertThat(actual.getField()).isEqualTo("testfield");
        assertThat(actual.getMatchPhrasePrefix()).isNotNull();
        assertThat(actual.getMatchPhrasePrefix().getQuery()).isEqualTo("testvalue");
    }

    @Test
    @DisplayName("throw exception for incorrect builder invocation for query with match phrase prefix")
    void shouldThrowExceptionForIncorrectBuilderMatchPhrasePrefixQuery() {
        assertThatThrownBy(() -> MatchPhrasePrefixQuery.builder().query("testvalue").maxExpansions(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field name can't be null");
        assertThatThrownBy(() -> MatchPhrasePrefixQuery.builder().field("testfield").maxExpansions(1).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Query can't be null");
    }

}
