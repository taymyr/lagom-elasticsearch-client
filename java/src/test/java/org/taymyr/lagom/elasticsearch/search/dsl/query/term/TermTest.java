package org.taymyr.lagom.elasticsearch.search.dsl.query.term;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

import static java.util.Arrays.asList;

class TermTest {

    @Nested
    class ExistsTest {

        @Test
        @DisplayName("successfully serialize search request with exists query")
        void shouldSuccessfullySerializeExists() {
            SearchRequest request = SearchRequest.builder()
                    .query(ExistsQuery.of("user"))
                    .build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_exists.json");
            assertThatJson(actual).isEqualTo(expected);
        }
    }

    @Nested
    class IdsTest {

        @Test
        @DisplayName("successfully serialize search request with ids query")
        void shouldSuccessfullySerializeIds() {
            SearchRequest request = SearchRequest.builder()
                    .query(IdsQuery.builder()
                            .type("_doc1")
                            .types("_doc2", "_doc3")
                            .values("1", "4", "100")
                            .build()
                    ).build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_ids.json");
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> IdsQuery.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Values can't be empty");
        }
    }

    @Nested
    class NumericRangeTest {

        @Test
        @DisplayName("successfully serialize search request with ids query")
        void shouldSuccessfullySerializeIds() {
            NumericRange range = NumericRange.builder()
                    .gt(9)
                    .gte(10)
                    .lt(21)
                    .lte(20)
                    .boost(2.0)
                    .build();
            SearchRequest request = SearchRequest.builder()
                    .query(RangeQuery.of("age", range))
                    .build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_numeric_range.json");
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> NumericRange.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("All field of NumericRange is null");
        }
    }

    @Nested
    class PrefixTest {

        @Test
        @DisplayName("successfully serialize search request with prefix query")
        void shouldSuccessfullySerializePrefixQuery() {
            SearchRequest request = SearchRequest.builder()
                    .query(PrefixQuery.builder()
                            .field("user")
                            .value("ki")
                            .boost(2.0)
                            .build()
                    )
                    .build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_prefix.json");
            assertThatJson(actual).isEqualTo(expected);
            request = SearchRequest.builder()
                    .query(PrefixQuery.of("user", "ki", 2.0))
                    .build();
            actual = serializeRequest(request, SearchRequest.class);
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> PrefixQuery.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Field name can't be null");
            assertThatThrownBy(() -> PrefixQuery.builder().field("field").build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Value can't be null");
        }
    }

    @Nested
    class RegexpTest {

        @Test
        @DisplayName("successfully serialize search request with regexp query")
        void shouldSuccessfullySerializeRegexp() {
            SearchRequest request = SearchRequest.builder()
                    .query(RegexpQuery.builder()
                            .field("name.first")
                            .value("s.*y")
                            .boost(1.2)
                            .flags(RegexpFlag.INTERSECTION, RegexpFlag.COMPLEMENT, RegexpFlag.EMPTY)
                            .maxDeterminizedStates(20000)
                            .build())
                    .build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_regexp.json");
            assertThatJson(actual).isEqualTo(expected);
            request = SearchRequest.builder()
                    .query(RegexpQuery.of("name.first", "s.*y", 1.2, "INTERSECTION|COMPLEMENT|EMPTY", 20000))
                    .build();
            actual = serializeRequest(request, SearchRequest.class);
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> RegexpQuery.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Field name can't be null");
            assertThatThrownBy(() -> RegexpQuery.builder().field("field").build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Value can't be null");
        }
    }

    @Nested
    class TermQueryTest {
        @Test
        @DisplayName("successfully serialize search request with term query")
        void shouldSuccessfullySerializeTerm() {
            SearchRequest request = SearchRequest.builder()
                    .query(BoolQuery.builder()
                            .should(TermQuery.builder()
                                    .field("status")
                                    .value("urgent")
                                    .boost(2.0)
                                    .build()
                            )
                            .should(TermQuery.of("status", "normal"))
                            .build()
                    ).build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_term.json");
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> TermQuery.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Field name can't be null");
            assertThatThrownBy(() -> TermQuery.builder().field("field").build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Value can't be null");
        }
    }

    @Nested
    class TermsTest {
        @Test
        @DisplayName("successfully serialize search request with terms query")
        void shouldSuccessfullySerializeTerms() {
            SearchRequest request = SearchRequest.builder()
                    .query(TermsQuery.builder()
                            .term("user", "kimchy", "elasticsearch")
                            .build()
                    ).build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_terms.json");
            assertThatJson(actual).isEqualTo(expected);
            request = new SearchRequest(
                    TermsQuery.of(ImmutableMap.of("user", asList("kimchy", "elasticsearch")))
            );
            actual = serializeRequest(request, SearchRequest.class);
            assertThatJson(actual).isEqualTo(expected);
        }
    }

    @Nested
    class WildcardTest {
        @Test
        @DisplayName("successfully serialize search request with wildcard query")
        void shouldSuccessfullySerializeTerm() {
            SearchRequest request = SearchRequest.builder()
                    .query(WildcardQuery.builder()
                            .field("user")
                            .value("ki*y")
                            .boost(2.0)
                            .build()
                    ).build();
            String actual = serializeRequest(request, SearchRequest.class);
            String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/request_wildcard.json");
            assertThatJson(actual).isEqualTo(expected);
            request = new SearchRequest(
                    WildcardQuery.of("user", "ki*y", 2.0)
            );
            actual = serializeRequest(request, SearchRequest.class);
            assertThatJson(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("throw exception for incorrect request")
        void shouldThrowExceptionForIncorrectRequest() {
            assertThatThrownBy(() -> WildcardQuery.builder().build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Field name can't be null");
            assertThatThrownBy(() -> WildcardQuery.builder().field("field").build())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Value can't be null");
        }
    }
}
