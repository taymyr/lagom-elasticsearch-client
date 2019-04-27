package org.taymyr.lagom.elasticsearch.search.dsl.query.suggest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.Fuzzy;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class SuggestTest {

    @Test
    @DisplayName("successfully serialize search request with suggest")
    void shouldSuccessfullySerializeSuggest() {
        SearchRequest request = SearchRequest.builder()
            .query(MatchQuery.of("name", "value"))
            .suggest("mySuggest", CompletionSuggest.builder()
                .prefix("prefix")
                .field("suggest")
                .fuzzy("override by next line")
                .fuzzy(Fuzzy.builder()
                    .fuzziness(1)
                    .transpositions(true)
                    .minLength(2)
                    .prefixLength(3)
                    .unicodeAware(true)
                    .build()
                ).context("name", "value")
                .skipDuplicates(false)
                .build()
            ).build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/suggest/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> CompletionSuggest.builder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Field 'prefix' can't be null");
        assertThatThrownBy(() -> CompletionSuggest.builder().prefix("prefix").build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Field 'field' can't be null");
    }

}
