package org.taymyr.lagom.elasticsearch.indices

import akka.Done
import com.lightbend.lagom.javadsl.api.transport.TransportErrorCode.NotFound
import com.lightbend.lagom.javadsl.api.transport.TransportException
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.maps.shouldContainKey
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
import org.taymyr.lagom.elasticsearch.indices.dsl.DynamicType
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.Companion.KEYWORD
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.Companion.OBJECT
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.Companion.TEXT
import org.taymyr.lagom.elasticsearch.search.ElasticSearchFullTextIT
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
                        "some_type" to Mapping(
                            mapOf(
                                "id" to MappingProperty.LONG,
                                "name" to MappingProperty(type = DataType.TEXT, analyzer = "autocomplete"),
                                "title" to MappingProperty.OBJECT,
                                "technicalName" to MappingProperty.TEXT,
                                "attachAllowed" to MappingProperty.BOOLEAN
                            )
                        )
                    )
                )
                whenReady(elasticIndices.create("test").invoke(request).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "test"
                }
            }
        }

        "Index with dynamic mappings" should {
            "successfully created" {
                val createDynamicDefault = CreateIndex(
                    Settings(1, 1),
                    mapOf("_doc" to Mapping(
                        mapOf("id" to MappingProperty.LONG)
                    ))
                )
                whenReady(elasticIndices.create("dynamic_default").invoke(createDynamicDefault).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "dynamic_default"
                }
                val createDynamicTrue = CreateIndex(
                    Settings(1, 1),
                    mapOf("_doc" to Mapping(
                        mapOf("id" to MappingProperty.LONG),
                        DynamicType.TRUE
                    ))
                )
                whenReady(elasticIndices.create("dynamic_true").invoke(createDynamicTrue).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "dynamic_true"
                }
                val createDynamicFalse = CreateIndex(
                    Settings(1, 1),
                    mapOf("_doc" to Mapping(
                        mapOf("id" to MappingProperty.LONG),
                        DynamicType.FALSE
                    ))
                )
                whenReady(elasticIndices.create("dynamic_false").invoke(createDynamicFalse).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "dynamic_false"
                }
                val createDynamicStrict = CreateIndex(
                    Settings(1, 1),
                    mapOf("_doc" to Mapping(
                        mapOf("id" to MappingProperty.LONG),
                        DynamicType.STRICT
                    ))
                )
                whenReady(elasticIndices.create("dynamic_strict").invoke(createDynamicStrict).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "dynamic_strict"
                }
                val createDynamicProperties = CreateIndex(
                    Settings(1, 1),
                    mapOf("_doc" to Mapping(
                        mapOf(
                            "name" to MappingProperty.TEXT,
                            "social_networks" to MappingProperty(
                                DataType.OBJECT,
                                dynamic = DynamicType.TRUE,
                                properties = mapOf()
                            )
                        ),
                        DynamicType.FALSE
                    ))
                )
                whenReady(elasticIndices.create("dynamic_props").invoke(createDynamicProperties).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "dynamic_props"
                }
            }
            "has correct mapping settings" {
                whenReady(elasticIndices.get("dynamic_default").invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey("dynamic_default")
                    result["dynamic_default"].let { index -> index!!
                        index.mappings.shouldContainKey("_doc")
                        index.mappings["_doc"]?.run {
                            dynamic shouldBe null
                        }
                    }
                }
                whenReady(elasticIndices.get("dynamic_true").invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey("dynamic_true")
                    result["dynamic_true"].let { index -> index!!
                        index.mappings.shouldContainKey("_doc")
                        index.mappings["_doc"]?.run {
                            dynamic shouldBe DynamicType.TRUE
                        }
                    }
                }
                whenReady(elasticIndices.get("dynamic_false").invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey("dynamic_false")
                    result["dynamic_false"].let { index -> index!!
                        index.mappings.shouldContainKey("_doc")
                        index.mappings["_doc"]?.run {
                            dynamic shouldBe DynamicType.FALSE
                        }
                    }
                }
                whenReady(elasticIndices.get("dynamic_strict").invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey("dynamic_strict")
                    result["dynamic_strict"]?.run {
                        mappings.shouldContainKey("_doc")
                        mappings["_doc"]?.run {
                            dynamic shouldBe DynamicType.STRICT
                        }
                    }
                }
                whenReady(elasticIndices.get("dynamic_props").invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey("dynamic_props")
                    result["dynamic_props"]?.run {
                        mappings.shouldContainKey("_doc")
                        mappings["_doc"]?.run {
                            dynamic shouldBe DynamicType.FALSE
                            properties.shouldContainKey("social_networks")
                            properties["social_networks"]?.dynamic shouldBe DynamicType.TRUE
                        }
                    }
                }
            }
        }

        "Index with aggregated fields" should {
            val aggregatedFieldsIndex = "aggregated_fields"
            val aggregatedFieldsDoc = "aggregated_fields"
            val aggregateTextField = "aggregate_text"
            val aggregateText = listOf(aggregateTextField)
            "successfully create the index" {
                val request = CreateIndex(
                    CreateIndex.Settings(1, 1),
                    mapOf(
                        aggregatedFieldsDoc to Mapping(
                            mapOf(
                                "user" to KEYWORD,
                                "message" to TEXT,
                                aggregateTextField to MappingProperty(type = DataType.TEXT, analyzer = "russian"),
                                "nested_obj" to OBJECT.copy(
                                    properties = mapOf(
                                        "text_field" to MappingProperty(type = DataType.TEXT, copyTo = aggregateText),
                                        "keyword_field" to MappingProperty(type = DataType.KEYWORD, copyTo = aggregateText),
                                        "integer_field" to MappingProperty(type = DataType.LONG, copyTo = aggregateText),
                                        "date_field" to MappingProperty(type = DataType.DATE, copyTo = aggregateText)
                                    )
                                )
                            )
                        )
                    )
                )
                whenReady(ElasticSearchFullTextIT.elasticIndices.create(aggregatedFieldsIndex).invoke(request).toCompletableFuture()) {
                    it.acknowledged shouldBe true
                    it.shardsAcknowledged shouldBe true
                    it.index shouldBe aggregatedFieldsIndex
                }
            }
            "has correct mappings" {
                whenReady(ElasticSearchFullTextIT.elasticIndices.get(aggregatedFieldsIndex).invoke().toCompletableFuture()) { result ->
                    result.shouldContainKey(aggregatedFieldsIndex)
                    result[aggregatedFieldsIndex].let { index -> index!!
                        index.mappings.shouldContainKey(aggregatedFieldsDoc)
                        index.mappings[aggregatedFieldsDoc]?.run {
                            properties.shouldContainKey(aggregateTextField)
                            properties[aggregateTextField]?.analyzer shouldBe "russian"
                            properties.shouldContainKey("nested_obj")
                            properties["nested_obj"]?.properties?.run {
                                shouldContainKey("text_field")
                                get("text_field")?.copyTo?.shouldContain(aggregateTextField)
                                shouldContainKey("keyword_field")
                                get("keyword_field")?.copyTo?.shouldContain(aggregateTextField)
                                shouldContainKey("integer_field")
                                get("integer_field")?.copyTo?.shouldContain(aggregateTextField)
                                shouldContainKey("date_field")
                                get("date_field")?.copyTo?.shouldContain(aggregateTextField)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}