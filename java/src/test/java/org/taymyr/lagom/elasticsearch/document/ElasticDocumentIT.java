package org.taymyr.lagom.elasticsearch.document;

import akka.Done;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.deser.ServiceCallKt;
import org.taymyr.lagom.elasticsearch.document.dsl.IndexDocumentResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkDelete;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkIndex;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult.BulkCommandResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;

@DisplayName("ElasticDocument should")
class ElasticDocumentIT  extends AbstractElasticsearchIT {

    private static SampleDocument document = new SampleDocument("user", "message");
    private static IndexedSampleDocument testEntity = new IndexedSampleDocument(new SampleDocument("tt", "mm"));

    @BeforeAll
    static void shouldSuccessfullyAddDocument() throws InterruptedException, ExecutionException, TimeoutException {
        IndexDocumentResult result = eventually(
                ServiceCallKt.invoke(elasticDocument.indexWithId("test", "sample", "1"), document)
        );
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
    }

    @Test
    @DisplayName("successfully check to exist a document")
    void shouldSuccessfullyCheckExistDocument() throws InterruptedException, ExecutionException, TimeoutException {
        Done result = eventually(elasticDocument.exists("test", "sample", "1").invoke());
        assertThat(result).isEqualTo(Done.getInstance());
    }

    @Test
    @DisplayName("successfully get a document by id")
    void shouldSuccessfullyGetDocumentById() throws InterruptedException, ExecutionException, TimeoutException {
        IndexedSampleDocument result = eventually(invokeT(elasticDocument.get("test", "sample", "1"), IndexedSampleDocument.class));
        assertThat(result.getSource()).isEqualTo(document);
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getVersion()).isEqualTo(1);
    }

    @Test
    @DisplayName("successfully check to exist a document source")
    void shouldSuccessfullyCheckExistDocumentSource() throws InterruptedException, ExecutionException, TimeoutException {
        Done result = eventually(elasticDocument.existsSource("test", "sample", "1").invoke());
        assertThat(result).isEqualTo(Done.getInstance());
    }

    @Test
    @DisplayName("successfully get a document source by id")
    void shouldSuccessfullyGetDocumentSourceById() throws InterruptedException, ExecutionException, TimeoutException {
        SampleDocument result = eventually(invokeT(elasticDocument.getSource("test", "sample", "1"), SampleDocument.class));
        assertThat(result).isEqualTo(document);
    }

    @Test
    @DisplayName("successfully add a document via bulk")
    void shouldSuccessfullyAddDocumentBulk() throws InterruptedException, ExecutionException, TimeoutException {
        BulkRequest request = BulkRequest.ofCommands(new BulkCreate("12", testEntity));
        BulkResult result = eventually(elasticDocument.bulk("test", "sample").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(1);
        BulkCommandResult item = result.getItems().get(0);
        assertThat(item.getStatus()).isEqualTo(201);
        assertThat(item.getResult()).isEqualTo("created");
        assertThat(item.getError()).isNull();
        assertThat(item.getIndex()).isEqualTo("test");
        assertThat(item.getType()).isEqualTo("sample");
        assertThat(item.getId()).isEqualTo("12");

        result = eventually(elasticDocument.bulk("test", "sample").invoke(request));
        assertThat(result.getErrors()).isTrue();
        assertThat(result.getItems()).hasSize(1);
        item = result.getItems().get(0);
        assertThat(item.getStatus()).isEqualTo(409);
        assertThat(item.getError()).isNotNull();
        assertThat(item.getError()).isInstanceOf(BulkResult.BulkCommandResult.ResultItemError.class);
        assertThat(item.getError().getReason()).containsIgnoringCase("document already exists");
        assertThat(item.getError().getType()).containsIgnoringCase("version_conflict_engine_exception");

        request = BulkRequest.ofCommands(new BulkIndex("12", testEntity));
        result = eventually(elasticDocument.bulk("test", "sample").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(1);
        item = result.getItems().get(0);
        assertThat(item.getStatus()).isEqualTo(200);
        assertThat(item.getResult()).isEqualTo("updated");

        request = BulkRequest.ofCommands(new BulkDelete("12"));
        result = eventually(elasticDocument.bulk("test", "sample").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(1);
        item = result.getItems().get(0);
        assertThat(item.getStatus()).isEqualTo(200);
        assertThat(item.getResult()).isEqualTo("deleted");
    }
}