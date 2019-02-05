package org.taymyr.lagom.elasticsearch.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocumentWithForcedNulls;
import org.taymyr.lagom.elasticsearch.document.dsl.delete.DeleteResult;
import org.taymyr.lagom.elasticsearch.document.dsl.update.DocUpdateRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.update.FullScriptedUpdateBody;
import org.taymyr.lagom.elasticsearch.document.dsl.update.FullScriptedUpdateRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateResult;

import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invoke;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;

class ElasticDocumentUpdateIT extends AbstractElasticsearchIT {

    private String uniqueIdA = UUID.randomUUID().toString();
    private String uniqueIdB = UUID.randomUUID().toString();

    @Test
    @DisplayName("ElasticDocumentUpdate should be correct")
    void shouldBeCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createDocumentUsingUpdate();
        updateExistingDocumentUsingPartialDocument();
        updateExistingDocumentUsingCompactString();
        createDocumentByFullScriptCommand();
        updateExistingDocumentUsingFullScriptCommand();
        updateExistingDocumentUsingFullScriptCommandNoParams();
        updateFieldsByNullValue();
        forceUpdateFields();
        deleteDocument();
    }

    // successfully create a document using update (upsert)
    void createDocumentUsingUpdate() throws InterruptedException, ExecutionException, TimeoutException {
        SampleDocument newDoc = new SampleDocument("user.original", "message.original");
        UpdateRequest updateRequest = UpdateRequest.docUpdate().doc(newDoc).docAsUpsert(true).build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdA), updateRequest)
            .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdA), IndexedSampleDocument.class));
        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdA);
        assertThat(result.getSource()).isEqualTo(newDoc);
    }

    // successfully update an existing document through merging with a partial document
    void updateExistingDocumentUsingPartialDocument() throws InterruptedException, ExecutionException, TimeoutException {
        SampleDocument partialDoc = new SampleDocument("user.updated", null, 30);
        UpdateRequest updateRequest = UpdateRequest.docUpdate().doc(partialDoc).build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdA), updateRequest)
            .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdA), IndexedSampleDocument.class));

        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdA);
        assertThat(result.getSource().getAge()).isEqualTo(30);
        assertThat(result.getSource().getUser()).isEqualTo("user.updated");
        assertThat(result.getSource().getMessage()).isEqualTo("message.original");
    }

    // successfully update an existing document using the compact script command
    void updateExistingDocumentUsingCompactString() throws InterruptedException, ExecutionException, TimeoutException {
        UpdateRequest updateRequest = UpdateRequest.scriptUpdate("ctx._source.age = 20");
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdA), updateRequest)
                .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdA), IndexedSampleDocument.class));

        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdA);
        assertThat(result.getSource().getAge()).isEqualTo(20);
        assertThat(result.getSource().getMessage()).isEqualTo("message.original");
    }

    // successfully create a new document using the full script command
    void createDocumentByFullScriptCommand() throws InterruptedException, ExecutionException, TimeoutException {
        String userFromParams = "user-from-params";
        FullScriptedUpdateBody<SampleDocument> script = FullScriptedUpdateBody.<SampleDocument>updateScript().lang("painless")
                    .source("ctx._source.balance += params.balance")
                    .params(new SampleDocument(userFromParams, null, null, 4.0))
                    .build();
        String userFromUpsert = "user-from-upsert";
        FullScriptedUpdateRequest<SampleDocument, SampleDocument> updateRequest = UpdateRequest.<SampleDocument, SampleDocument>scriptUpdate()
                .script(script)
                .upsert(new SampleDocument(userFromUpsert, null, null, 1.0))
                .build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdB), updateRequest)
                    .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdB), IndexedSampleDocument.class));

        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getSource().getBalance()).isEqualTo(1.0);
        assertThat(result.getSource().getUser()).isEqualTo(userFromUpsert);
    }

    // successfully update an existing document using the full script command
    void updateExistingDocumentUsingFullScriptCommand() throws InterruptedException, ExecutionException, TimeoutException {
        String userFromParams = "user-from-params";
        FullScriptedUpdateBody<SampleDocument> script = FullScriptedUpdateBody.<SampleDocument>updateScript()
                .source("ctx._source.balance += params.balance")
                .params(new SampleDocument(userFromParams, null, null, 4.0))
                .build();
        String userFromUpsert = "user-from-upsert";
        UpdateRequest updateRequest = UpdateRequest.<SampleDocument, SampleDocument>scriptUpdate()
                .script(script)
                .upsert(new SampleDocument(userFromUpsert, null, null, 1.0))
                .build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdB), updateRequest)
                    .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdB), IndexedSampleDocument.class));
        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getSource().getBalance()).isEqualTo(5.0);
        assertThat(result.getSource().getUser()).isEqualTo(userFromUpsert);
    }

    // successfully update an existing document using the full script command (no params and no upsert)
    void updateExistingDocumentUsingFullScriptCommandNoParams() throws InterruptedException, ExecutionException, TimeoutException {
        FullScriptedUpdateBody<SampleDocument> script = FullScriptedUpdateBody.<SampleDocument>updateScript()
                .source("ctx._source.balance = 200.0")
                .build();
        FullScriptedUpdateRequest<SampleDocument, SampleDocument> updateRequest =
                UpdateRequest.<SampleDocument, SampleDocument>scriptUpdate().script(script).build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdB), updateRequest)
                    .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdB), IndexedSampleDocument.class));
        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getSource().getBalance()).isEqualTo(200.0);
    }

    // successfully update fields existing document with nulls
    void updateFieldsByNullValue () throws InterruptedException, ExecutionException, TimeoutException {
        String user = "user-with-nulls";
        DocUpdateRequest<SampleDocumentWithForcedNulls> updateRequest = UpdateRequest.<SampleDocumentWithForcedNulls>docUpdate()
                .doc(new SampleDocumentWithForcedNulls(user))
                .build();
        CompletionStage<IndexedSampleDocument> future = invoke(elasticDocument.update("test", "sample", uniqueIdB), updateRequest)
                .thenCompose(result -> invokeT(elasticDocument.get("test", "sample", uniqueIdB), IndexedSampleDocument.class));
        IndexedSampleDocument result = eventually(future);
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getSource().getBalance()).isNull();
        assertThat(result.getSource().getMessage()).isNull();
        assertThat(result.getSource().getAge()).isNull();
        assertThat(result.getSource().getUser()).isEqualTo(user);
    }

    // successfully force update fields of an existent document(disable implicit 'detect_noop' behaviour)
    void forceUpdateFields() throws InterruptedException, ExecutionException, TimeoutException {
        String user = "user-with-nulls";
        DocUpdateRequest<SampleDocument> updateRequest = UpdateRequest.<SampleDocument>docUpdate()
                .doc(new SampleDocument(user))
                .detectNoOp(false)
                .build();
        UpdateResult result = eventually(invoke(elasticDocument.update("test", "sample", uniqueIdB), updateRequest));
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getResult()).isEqualTo("updated");
    }

    // successfully delete an existing document
    void deleteDocument() throws InterruptedException, ExecutionException, TimeoutException {
        DeleteResult result = eventually(elasticDocument.delete("test", "sample", uniqueIdB).invoke());
        assertThat(result.getId()).isEqualTo(uniqueIdB);
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getType()).isEqualTo("sample");
        assertThat(result.getResult()).isEqualTo("deleted");
    }
}