package org.taymyr.lagom.elasticsearch.document.dsl.delete;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;

class DeleteDocumentTest {

    @Test
    @DisplayName("successfully deserialize update result")
    void shouldSuccessfullyDeserializeUpdateResult() {
        DeleteResult result = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/delete/result.json"),
                DeleteResult.class
        );
        assertThat(result.getIndex()).isEqualTo("twitter");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getType()).isEqualTo("_doc");
        assertThat(result.getResult()).isEqualTo("deleted");
        assertThat(result.getVersion()).isEqualTo(2);
    }
}