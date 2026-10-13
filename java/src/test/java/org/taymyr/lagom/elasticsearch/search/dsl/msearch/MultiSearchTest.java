package org.taymyr.lagom.elasticsearch.search.dsl.msearch;

import kotlin.NotImplementedError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.TestDocumentResult;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.MatchAllQuery;

import java.util.Collections;
import java.util.Objects;

import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class MultiSearchTest {

    private static final String RESOURCES = "org/taymyr/lagom/elasticsearch/search/dsl/msearch/";
    private static final SearchRequest search = SearchRequest.builder().query(MatchAllQuery.of()).build();


    @Test
    @DisplayName("should throw on creating deserializer")
    void shouldThrowWhenCreatingDeserializer() {
        assertThatThrownBy(() -> deserializeResponse("", MultiSearchRequest.class))
                .isInstanceOf(NotImplementedError.class);
    }

    @Test
    @DisplayName("should not create empty request")
    void shouldNotCreateEmptyRequest() {
        assertThatThrownBy(() -> MultiSearchRequest.of(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("successfully serialize search with index in header")
    void shouldSerializeWithIndex() {
        MultiSearchRequest request = MultiSearchRequest.of(MultiSearchItem.of("test", search));
        String actual = serializeRequest(request, MultiSearchRequest.class);
        assertNdjson(actual, "header-index.ndjson");
    }

    @Test
    @DisplayName("successfully serialize search with empty header")
    void shouldSerializeWithEmptyHeader() {
        MultiSearchRequest request = MultiSearchRequest.of(new MultiSearchItem(search));
        String actual = serializeRequest(request, MultiSearchRequest.class);
        assertNdjson(actual, "header-empty.ndjson");
    }

    @Test
    @DisplayName("successfully serialize all fields of header")
    void shouldSerializeAllHeaderFields() {
        MultiSearchHeader header = new MultiSearchHeader(asList("test1", "test2"), "route", "_local", true);
        MultiSearchRequest request = MultiSearchRequest.of(new MultiSearchItem(search, header));
        String actual = serializeRequest(request, MultiSearchRequest.class);
        assertNdjson(actual, "header-all-fields.ndjson");
    }

    @Test
    @DisplayName("successfully serialize several searches in order")
    void shouldSerializeSeveralSearchesInOrder() {
        SearchRequest page = SearchRequest.builder().query(MatchAllQuery.of()).from(10).size(20).build();
        MultiSearchRequest request = MultiSearchRequest.of(
                MultiSearchItem.of("test1", search),
                MultiSearchItem.of("test2", page)
        );
        String actual = serializeRequest(request, MultiSearchRequest.class);
        assertNdjson(actual, "several-searches.ndjson");
    }

    @Test
    @DisplayName("successfully deserialize multi search result")
    void shouldDeserializeResult() {
        MultiSearchResult actual = deserializeResponse(
                resourceAsString(RESOURCES + "result.json"),
                MultiSearchResult.class
        );
        assertThat(actual.getTook()).isEqualTo(5L);
        assertThat(actual.getResponses()).hasSize(3);

        assertThat(actual.isError(0)).isFalse();
        assertThat(actual.status(0)).isEqualTo(200);
        assertThat(actual.error(0)).isNull();
        TestDocumentResult first = actual.result(0, TestDocumentResult.class);
        assertThat(first.getHits().getTotal().getValue()).isEqualTo(2);
        assertThat(first.getHits().getHits()).hasSize(2);
        assertThat(first.getSources()).containsExactly(
                new TestDocument("user1", "message1"),
                new TestDocument("user2", "message2")
        );

        assertThat(first.getTyped("/hits/total/value", Integer.class)).isEqualTo(2);

        assertThat(actual.isError(1)).isFalse();
        TestDocumentResult second = actual.result(1, TestDocumentResult.class);
        assertThat(second.getHits().getTotal().getValue()).isEqualTo(0);
        assertThat(second.getHits().getHits()).isEmpty();

        assertThat(actual.isError(2)).isTrue();
        assertThat(actual.status(2)).isEqualTo(404);
        assertThat(Objects.requireNonNull(actual.error(2)).get("type").asText()).isEqualTo("index_not_found_exception");
        assertThatThrownBy(() -> actual.result(2, TestDocumentResult.class))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("index_not_found_exception");
    }

    private static void assertNdjson(String actual, String expectedResource) {
        String expected = resourceAsString(RESOURCES + expectedResource);
        assertThat(actual).endsWith("\n").doesNotContain("\n\n");
        String[] actualLines = actual.split("\n");
        Assertions.assertNotNull(expected);
        String[] expectedLines = expected.split("\n");
        assertThat(actualLines).hasSameSizeAs(expectedLines);
        for (int i = 0; i < expectedLines.length; i++) {
            assertThatJson(actualLines[i]).isEqualTo(expectedLines[i]);
        }
    }
}
