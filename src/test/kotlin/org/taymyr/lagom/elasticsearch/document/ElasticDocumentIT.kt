package org.taymyr.lagom.elasticsearch.document

import akka.Done
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.string.containIgnoringCase
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequestFabric
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult

class ElasticDocumentIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        // TODO(Ilya Korshunov): add tests for update bulk command
        "ElasticDocument" should {
            val document = SampleDocument("user", "message")
            "successfully add a document to index" {
                whenReady(elasticDocument.indexWithId("test", "sample", "1")
                    .invoke(document).toCompletableFuture()) { result ->
                    result.index shouldBe "test"
                    result.type shouldBe "sample"
                }
            }
            "successfully check to exist a document" {
                whenReady(elasticDocument.exists("test", "sample", "1")
                    .invoke().toCompletableFuture()) { result ->
                    result shouldBe Done.getInstance()
                }
            }
            "successfully get a document by id" {
                whenReady(elasticDocument.get("test", "sample", "1")
                    .invokeT<IndexedSampleDocument>().toCompletableFuture()) { result ->
                    result.source shouldBe document
                    result.index shouldBe "test"
                    result.type shouldBe "sample"
                    result.id shouldBe "1"
                    result.version shouldBe 1
                }
            }
            "successfully check to exist a document source" {
                whenReady(elasticDocument.existsSource("test", "sample", "1")
                    .invoke().toCompletableFuture()) { result ->
                    result shouldBe Done.getInstance()
                }
            }
            "successfully get a document source by id" {
                whenReady(elasticDocument.getSource("test", "sample", "1")
                    .invokeT<SampleDocument>().toCompletableFuture()) { result ->
                    result shouldBe document
                }
            }
            val testEntity = IndexedSampleDocument(SampleDocument("tt", "mm"))
            "successfully add a document via bulk" {
                val request = BulkRequestFabric()
                    .newCommand().forId("12").withElement(testEntity).create()
                    .complete()
                whenReady(elasticDocument.bulk("test", "sample").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 1
                    val item = result.items[0]
                    item.status shouldBe 201
                    item.command shouldBe "create"
                    item.result shouldBe "created"
                    item.error shouldBe null
                    item.index shouldBe "test"
                    item.type shouldBe "sample"
                    item.id shouldBe "12"
                }
            }
            "error on creating exists document" {
                val request = BulkRequestFabric()
                    .newCommand().forId("12").withElement(testEntity).create()
                    .complete()
                whenReady(elasticDocument.bulk("test", "sample").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe true
                    result.items shouldHaveSize 1
                    val item = result.items[0]
                    item.status shouldBe 409
                    item.command shouldBe "create"
                    item.error shouldBe beInstanceOf(BulkResult.ResultItemError::class)
                    item.error shouldNotBe null
                    item.error!!.reason shouldHave containIgnoringCase("document already exists")
                    item.error!!.type shouldBe "version_conflict_engine_exception"
                }
            }
            "successful index exists document" {
                val request = BulkRequestFabric()
                    .newCommand().forId("12").withElement(testEntity).index()
                    .complete()
                whenReady(elasticDocument.bulk("test", "sample").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 1
                    val item = result.items[0]
                    item.command shouldBe "index"
                    item.status shouldBe 200
                    item.result shouldBe "updated"
                }
            }
            "successful delete document" {
                val request = BulkRequestFabric()
                    .newCommand().forId("12").delete()
                    .complete()
                whenReady(elasticDocument.bulk("test", "sample").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 1
                    val item = result.items[0]
                    item.command shouldBe "delete"
                    item.status shouldBe 200
                    item.result shouldBe "deleted"
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}