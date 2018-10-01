package org.taymyr.lagom.elasticsearch.indices

import akka.Done
import com.lightbend.lagom.javadsl.api.transport.TransportErrorCode.NotFound
import com.lightbend.lagom.javadsl.api.transport.TransportException
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.maps.shouldContainKeys
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Index
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings
import java.util.concurrent.ExecutionException

class ElasticIndicesIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        val def = Pair("def", CreateIndex())
        val custom = Pair("custom", CreateIndex(Settings(Index(3, 3))))

        "Index" should {
            "created without settings" {
                whenReady(elasticIndices.create(def.first).invoke(def.second).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe def.first
                }
                whenReady(elasticIndices.create(custom.first).invoke(custom.second).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe custom.first
                }
            }
            "exists after created" {
                whenReady(elasticIndices.exists(listOf(def.first)).invoke().toCompletableFuture()) { result ->
                    result shouldBe Done.getInstance()
                }
                whenReady(elasticIndices.exists(listOf(custom.first)).invoke().toCompletableFuture()) { result ->
                    result shouldBe Done.getInstance()
                }
                whenReady(elasticIndices.exists(listOf(def.first, custom.first)).invoke().toCompletableFuture()) { result ->
                    result shouldBe Done.getInstance()
                }
            }
            "not be exists if not created" {
                val notFound = shouldThrow<ExecutionException> {
                    whenReady(elasticIndices.exists(listOf("test2")).invoke().toCompletableFuture()) {}
                }
                notFound.cause shouldBe beInstanceOf<TransportException>()
                (notFound.cause as TransportException).errorCode() shouldBe NotFound
            }
            "can get after created" {
                whenReady(elasticIndices.get(listOf(def.first, custom.first)).invoke().toCompletableFuture()) { result ->
                    result.shouldContainKeys(def.first, custom.first)
                    result[def.first].let { index -> index!!
                        index.settings.index.run {
                            numberOfReplicas shouldBe 1
                            numberOfShards shouldBe 5
                        }
                    }
                    result[custom.first].let { index -> index!!
                        index.settings.index.run {
                            numberOfReplicas shouldBe custom.second.settings?.index?.numberOfReplicas
                            numberOfShards shouldBe custom.second.settings?.index?.numberOfShards
                        }
                    }
                }
            }
            "deleted" {
                whenReady(elasticIndices.delete(listOf(def.first, custom.first)).invoke().toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}