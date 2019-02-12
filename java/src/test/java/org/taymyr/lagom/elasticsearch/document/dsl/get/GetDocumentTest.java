package org.taymyr.lagom.elasticsearch.document.dsl.get;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.IndexedTestDocument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;

class GetDocumentTest {

    @Test
    @DisplayName("successfully deserialize indexed document")
    void shouldSuccessfullyDeserializeIndexedDocument() {
        IndexedTestDocument document = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/document/dsl/get/result.json"),
                IndexedTestDocument.class
        );
        assertThat(document.getIndex()).isEqualTo("twitter");
        assertThat(document.getId()).isEqualTo("0");
        assertThat(document.getType()).isEqualTo("_doc");
        assertThat(document.getVersion()).isEqualTo(1);
        assertThat(document.isFound()).isTrue();
        assertThat(document.getSource()).isEqualTo(new TestDocument("kimchy", "trying out Elasticsearch"));
    }

}
