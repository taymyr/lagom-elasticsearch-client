package org.taymyr.lagom.elasticsearch.document

import akka.Done
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT

class ElasticDocumentIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
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
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}