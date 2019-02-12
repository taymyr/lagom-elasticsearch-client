package org.taymyr.lagom.elasticsearch.document.dsl.index;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;

class IndexDocumentTest {

    @Test
    @DisplayName("successfully deserialize update result")
    void shouldSuccessfullyDeserializeUpdateResult() {
        IndexResult result = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/index/result.json"),
                IndexResult.class
        );
        assertThat(result.getIndex()).isEqualTo("twitter");
        assertThat(result.getType()).isEqualTo("_doc");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getVersion()).isEqualTo(1);
        assertThat(result.getResult()).isEqualTo("created");
    }

}