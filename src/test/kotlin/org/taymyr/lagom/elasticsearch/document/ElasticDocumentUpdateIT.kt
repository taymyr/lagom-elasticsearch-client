package org.taymyr.lagom.elasticsearch.document

import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentWithForcedNulls
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.document.dsl.delete.DeleteResult
import org.taymyr.lagom.elasticsearch.document.dsl.update.FullScriptedUpdateBody
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateRequest
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateResult
import java.util.UUID

class ElasticDocumentUpdateIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "ElasticDocumentUpdate" should {
            val uniqueIdA = UUID.randomUUID().toString()
            val uniqueIdB = UUID.randomUUID().toString()
            "successfully create a document using update (upsert)" {
                val newDoc = SampleDocument("user.original", "message.original")
                val updateRequest = UpdateRequest.docUpdate<SampleDocument>().doc(newDoc).docAsUpsert(true).build()
                val future = elasticDocument.update("test", "sample", uniqueIdA).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdA).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdA
                    it.source shouldBe newDoc
                }
            }
            "successfully update an existing document through merging with a partial document" {
                val partialDoc = SampleDocument(user = "user.updated", age = 30)
                val updateRequest = UpdateRequest.docUpdate<SampleDocument>().doc(partialDoc).build()
                val future = elasticDocument.update("test", "sample", uniqueIdA).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdA).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdA
                    it.source.age shouldBe 30
                    it.source.user shouldBe "user.updated"
                    it.source.message shouldBe "message.original"
                }
            }
            "successfully update an existing document using the compact script command" {
                val updateRequest = UpdateRequest.scriptUpdate("ctx._source.age = 20")
                val future = elasticDocument.update("test", "sample", uniqueIdA).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdA).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdA
                    it.source.age shouldBe 20
                    it.source.message shouldBe "message.original"
                }
            }
            "successfully create a new document using the full script command" {
                val userFromParams = "user-from-params"
                val script = FullScriptedUpdateBody.updateScript<SampleDocument>().lang("painless")
                    .source("ctx._source.balance += params.balance")
                    .params(SampleDocument(user = userFromParams, balance = 4.0))
                    .build()
                val userFromUpsert = "user-from-upsert"
                val updateRequest = UpdateRequest.scriptUpdate<SampleDocument, SampleDocument>().script(script)
                    .upsert(SampleDocument(user = userFromUpsert, balance = 1.0))
                    .build()
                val future = elasticDocument.update("test", "sample", uniqueIdB).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdB).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdB
                    it.source.balance shouldBe 1.0
                    it.source.user shouldBe userFromUpsert
                }
            }
            "successfully update an existing document using the full script command" {
                val userFromParams = "user-from-params"
                val script = FullScriptedUpdateBody.updateScript<SampleDocument>()
                    .source("ctx._source.balance += params.balance")
                    .params(SampleDocument(user = userFromParams, balance = 4.0))
                    .build()
                val userFromUpsert = "user-from-upsert"
                val updateRequest = UpdateRequest.scriptUpdate<SampleDocument, SampleDocument>().script(script)
                    .upsert(SampleDocument(user = userFromUpsert, balance = 1.0))
                    .build()
                val future = elasticDocument.update("test", "sample", uniqueIdB).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdB).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdB
                    it.source.balance shouldBe 5.0
                    it.source.user shouldNotBe userFromParams
                }
            }
            "successfully update an existing document using the full script command (no params and no upsert)" {
                val script = FullScriptedUpdateBody.updateScript<SampleDocument>()
                    .source("ctx._source.balance = 200.0")
                    .build()
                val updateRequest = UpdateRequest.scriptUpdate<SampleDocument, SampleDocument>().script(script)
                    .build()
                val future = elasticDocument.update("test", "sample", uniqueIdB).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdB).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdB
                    it.source.balance shouldBe 200.0
                }
            }
            "successfully update fields existing document with nulls" {
                val user = "user-with-nulls"
                val updateRequest = UpdateRequest.docUpdate<SampleDocumentWithForcedNulls>()
                    .doc(SampleDocumentWithForcedNulls(user = user))
                    .build()
                val future = elasticDocument.update("test", "sample", uniqueIdB).invoke(updateRequest)
                    .thenCompose { elasticDocument.get("test", "sample", uniqueIdB).invokeT<IndexedSampleDocument>() }
                    .toCompletableFuture()
                whenReady(future) {
                    it.id shouldBe uniqueIdB
                    it.source.balance shouldBe null
                    it.source.message shouldBe null
                    it.source.age shouldBe null
                    it.source.user shouldBe user
                }
            }
            "successfully force update fields of an existent document(disable implicit 'detect_noop' behaviour)" {
                val user = "user-with-nulls"
                val updateRequest = UpdateRequest.docUpdate<SampleDocument>()
                    .doc(SampleDocument(user = user))
                    .detectNoOp(false)
                    .build()
                whenReady(elasticDocument.update("test", "sample", uniqueIdB).invoke(updateRequest).toCompletableFuture()) {
                    it shouldBe beInstanceOf(UpdateResult::class)
                    it.index shouldBe "test"
                    it.type shouldBe "sample"
                    it.result shouldBe "updated"
                    it.id shouldBe uniqueIdB
                }
            }
            "successfully delete an existing document" {
                whenReady(elasticDocument.delete("test", "sample", uniqueIdB).invoke().toCompletableFuture()) {
                    it shouldBe beInstanceOf(DeleteResult::class)
                    it.index shouldBe "test"
                    it.type shouldBe "sample"
                    it.result shouldBe "deleted"
                    it.id shouldBe uniqueIdB
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}