package org.taymyr.lagom.elasticsearch.document.dsl.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class UpdateDocumentTest {

    @Test
    @DisplayName("successfully serialize a document update (upsert)")
    void shouldSuccessfullySerializeDocumentUpdateUpsert() {
        TestDocument newDoc = new TestDocument("user.original", "message.original");
        DocUpdateRequest request = DocUpdateRequest.builder()
                .doc(newDoc)
                .docAsUpsert(true)
                .detectNoOp(false)
                .build();
        String actual = serializeRequest(request, DocUpdateRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/update/request_upsert.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> DocUpdateRequest.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'doc' can not be null");
        assertThatThrownBy(() -> FullScriptedUpdateBody.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'source' can not be null");
        assertThatThrownBy(() -> FullScriptedUpdateRequest.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'script' can not be null");
    }

    @Test
    @DisplayName("successfully serialize a partial document update")
    void shouldSuccessfullySerializePartialDocumentUpdate() {
        TestDocument partialDoc = new TestDocument("user.updated", null);
        DocUpdateRequest request = new DocUpdateRequest<>(partialDoc);
        String actual = serializeRequest(request, DocUpdateRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/update/request_partial.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize update request by compact script command")
    void shouldSuccessfullySerializeUpdateByCompactScriptCommand() {
        ShortScriptedUpdateRequest request = new ShortScriptedUpdateRequest("ctx._source.age = 20");
        String actual = serializeRequest(request, ShortScriptedUpdateRequest.class);
        assertThatJson(actual).isEqualTo("{\"script\":\"ctx._source.age = 20\"}");
    }

    @Test
    @DisplayName("successfully serialize update request by full script command")
    void shouldSuccessfullySerializeUpdateByFullScriptCommand() {
        FullScriptedUpdateRequest<TestDocument, TestDocument> request = FullScriptedUpdateRequest.<TestDocument, TestDocument>builder()
                .script(FullScriptedUpdateBody.<TestDocument>builder()
                        .id("123")
                        .lang("painless")
                        .source("ctx._source.balance += params.balance")
                        .params(new TestDocument("user-from-params", null, 4.0))
                        .build()
                )
                .upsert(new TestDocument("user-from-upsert", null, 1.0))
                .scriptedUpsert(false)
                .build();
        String actual = serializeRequest(request, FullScriptedUpdateRequest.class);
        assertThatJson(actual).isEqualTo(resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/update/request_script.json"));
    }

    @Test
    @DisplayName("successfully deserialize update result")
    void shouldSuccessfullyDeserializeUpdateResult() {
        UpdateResult result = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/update/result.json"),
                UpdateResult.class
        );
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getType()).isEqualTo("_doc");
        assertThat(result.getResult()).isEqualTo("noop");
        assertThat(result.getVersion()).isEqualTo(7);
    }
}