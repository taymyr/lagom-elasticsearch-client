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
import org.taymyr.lagom.elasticsearch.AutocompleteFilter
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings
import org.taymyr.lagom.elasticsearch.indices.dsl.CustomAnalyzer
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import java.util.concurrent.ExecutionException

class ElasticIndicesIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        val def = Pair("def", CreateIndex())
        val custom = Pair("custom", CreateIndex(Settings(3, 3)))

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
                            numberOfReplicas shouldBe custom.second.settings?.numberOfReplicas
                            numberOfShards shouldBe custom.second.settings?.numberOfShards
                        }
                    }
                }
            }
            "deleted" {
                whenReady(elasticIndices.delete(listOf(def.first, custom.first)).invoke().toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                }
            }
            "created with filter" {
                val request = CreateIndex(
                    Settings(1, 1, CreateIndex.Analysis(
                        mapOf(
                            "autocomplete_filter" to AutocompleteFilter(
                                "edge_ngram",
                                1,
                                20
                            )
                        ),
                        mapOf(
                            "autocomplete" to CustomAnalyzer(
                                "standard",
                                listOf(
                                    "lowercase",
                                    "autocomplete_filter"
                                )
                            )
                        )
                    )),
                    mapOf(
                        "some_type" to CreateIndex.Mapping(mapOf(
                            "id" to MappingProperty.LONG,
                            "name" to MappingProperty(type = DataType.TEXT, analyzer = "autocomplete"),
                            "title" to MappingProperty.OBJECT,
                            "technicalName" to MappingProperty.TEXT,
                            "attachAllowed" to MappingProperty.BOOLEAN
                        ))
                    )
                )
                whenReady(elasticIndices.create("test").invoke(request).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "test"
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}