package org.taymyr.lagom.elasticsearch.document;

import akka.Done;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.TestDocument;
import org.taymyr.lagom.elasticsearch.TestDocument.IndexedTestDocument;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkUpdate;
import org.taymyr.lagom.elasticsearch.document.dsl.delete.DeleteResult;
import org.taymyr.lagom.elasticsearch.document.dsl.index.IndexResult;
import org.taymyr.lagom.elasticsearch.document.dsl.update.DocUpdateRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.ServiceCall.invoke;

import static java.lang.Thread.sleep;

class ElasticDocumentIT extends AbstractElasticsearchIT {

    @Test
    @DisplayName("Document service descriptor should work correct")
    void shouldWorkCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        indexDocument();
        sleep(1000);
        bulkUpdate();
        sleep(1000);
        getDocument();
        update();
        sleep(1000);
        getSource();
        checkExists();
        delete();
    }

    private void indexDocument() throws InterruptedException, ExecutionException, TimeoutException {
        IndexResult result = eventually(invoke(elasticDocument.indexWithId("test", "sample", "1"),
                new TestDocument("user", "message")));
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
    }

    private void getDocument() throws InterruptedException, ExecutionException, TimeoutException {
        IndexedTestDocument result = eventually(invoke(elasticDocument.get("test", "sample", "1"), IndexedTestDocument.class));
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getSource().getUser()).isEqualTo("user.bulkUpdate");
        assertThat(result.getSource().getMessage()).isEqualTo("message");
    }

    private void getSource() throws InterruptedException, ExecutionException, TimeoutException {
        TestDocument result = eventually(invoke(elasticDocument.getSource("test", "sample", "1"), TestDocument.class));
        assertThat(result.getUser()).isEqualTo("user.update");
        assertThat(result.getMessage()).isEqualTo("message");
    }

    private void checkExists() throws InterruptedException, ExecutionException, TimeoutException {
        Done result = eventually(elasticDocument.exists("test", "sample", "1").invoke());
        assertThat(result).isEqualTo(Done.getInstance());
        result = eventually(elasticDocument.existsSource("test", "sample", "1").invoke());
        assertThat(result).isEqualTo(Done.getInstance());
    }

    private void bulkUpdate() throws InterruptedException, ExecutionException, TimeoutException {
        BulkRequest request = BulkRequest.of(
                new BulkUpdate("1", new IndexedTestDocument(new TestDocument("user.bulkUpdate", "message")))
        );
        BulkResult result = eventually(elasticDocument.bulk("test", "sample").invoke(request));
        assertThat(result.isErrors()).isFalse();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getStatus()).isEqualTo(200);
        assertThat(result.getItems().get(0).getResult()).isEqualTo("updated");
        assertThat(result.getItems().get(0).getError()).isNull();
        assertThat(result.getItems().get(0).getIndex()).isEqualTo("test");
        assertThat(result.getItems().get(0).getType()).isEqualTo("sample");
        assertThat(result.getItems().get(0).getId()).isEqualTo("1");
    }

    private void update() throws InterruptedException, ExecutionException, TimeoutException {
        TestDocument doc = new TestDocument("user.update", "message");
        UpdateRequest updateRequest = DocUpdateRequest.builder().doc(doc).build();
        UpdateResult result = eventually(invoke(elasticDocument.update("test", "sample", "1"), updateRequest));
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getResult()).isEqualTo("updated");
    }

    private void delete() throws InterruptedException, ExecutionException, TimeoutException {
        DeleteResult result = eventually(elasticDocument.delete("test", "sample", "1").invoke());
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getResult()).isEqualTo("deleted");
    }

}
