package org.taymyr.lagom.elasticsearch.document.dsl.bulk;

import kotlin.NotImplementedError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.IndexedTestDocument;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.BulkCreateResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.BulkDeleteResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.BulkIndexResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.BulkUpdateResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.ResultItemError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class BulkTest {

    private IndexedTestDocument testEntity = new IndexedTestDocument(new TestDocument("user", "test"));

    @Test
    @DisplayName("should throwable on creating deserializer")
    void shouldThrowWhenCreatingDeserializer() {
        assertThatThrownBy(() -> deserializeResponse("", BulkRequest.class))
                .isInstanceOf(NotImplementedError.class);
    }

    @Test
    @DisplayName("successfully serialize bulk delete")
    void shouldSuccessfullySerializeBulkDelete() {
        BulkRequest request = BulkRequest.of(new BulkDelete("1"));
        String actual = serializeRequest(request, BulkRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/bulk/delete.json");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize bulk create")
    void shouldSuccessfullySerializeBulkCreate() {
        BulkRequest request = BulkRequest.of(new BulkCreate("1", testEntity));
        String actual = serializeRequest(request, BulkRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/bulk/create.json");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize bulk index")
    void shouldSuccessfullySerializeBulkIndex() {
        BulkRequest request = BulkRequest.of(new BulkIndex("1", testEntity));
        String actual = serializeRequest(request, BulkRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/bulk/index.json");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize bulk update")
    void shouldSuccessfullySerializeBulkUpdate() {
        IndexedTestDocument testEntityWithNull = new IndexedTestDocument(new TestDocument("test", null));
        BulkRequest request = BulkRequest.of(new BulkUpdate("1", testEntityWithNull));
        String actual = serializeRequest(request, BulkRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/bulk/update.json");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully deserialize bulk result")
    void shouldSuccessfullyDeserializeBulkResult() {
        BulkResult actual = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/bulk/result.json"),
                BulkResult.class
        );
        assertThat(actual.isErrors()).isFalse();
        assertThat(actual.getTook()).isEqualTo(30);
        assertThat(actual.getItems()).hasSize(5);

        BulkIndexResult indexResult = new BulkIndexResult("test", "_doc", "1", 201, "created", null);
        BulkUpdateResult updateResult = new BulkUpdateResult("test", "_doc", "1", 200, "updated", null);
        BulkDeleteResult deleteResult = new BulkDeleteResult("test", "_doc", "2", 404, "not_found", null);
        BulkCreateResult createResult = new BulkCreateResult("test", "_doc", "3", 201, "created", null);
        BulkDeleteResult errorResult = new BulkDeleteResult("test", "_doc", "4", 500, "error", new ResultItemError("err_type", "err_reason"));
        assertThat(actual.getItems()).containsExactlyInAnyOrder(indexResult, updateResult, deleteResult, createResult, errorResult);
    }
}
