package org.taymyr.lagom.elasticsearch

import com.fasterxml.jackson.annotation.JsonProperty
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Mapping
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.CompositeAggregation
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.FilterAggregation
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.NestedAggregation
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation.FieldSpec
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.joining.NestedQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.joining.NestedQueryBody
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Term
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermsQuery
import java.lang.Thread.sleep
import java.util.Date

class AggregationsIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "test" should {
            "successfully create new index" {
                val request = CreateIndex(
                    Settings(1, 1),
                    mapOf(
                        "product" to Mapping(mapOf(
                            "id" to MappingProperty.LONG,
                            "category.id" to MappingProperty.LONG,
                            "category.title" to MappingProperty.KEYWORD,
                            "fullTextBoosted" to MappingProperty(DataType.TEXT, "russian"),
                            "fullText" to MappingProperty(DataType.TEXT, "russian"),
                            "sellerId" to MappingProperty.LONG,
                            "categoryIds" to MappingProperty.LONG,
                            "updateDate" to MappingProperty.DATE,
                            "basePrice" to MappingProperty.INTEGER,
                            "staticFacets" to MappingProperty(DataType.NESTED, null, mapOf(
                                "name" to MappingProperty.KEYWORD,
                                "value" to MappingProperty.KEYWORD
                            )),
                            "payload" to MappingProperty.OBJECT
                        ))
                    )
                )
                whenReady(elasticIndices.create("product").invoke(request).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "product"
                }
            }
            "successfully add some items" {
                val testEntity = IndexedSampleProduct(SampleProduct(
                    13,
                    SampleProduct.Category(100, "Ягоды"),
                    "Клубника вкусная",
                    "Россия Ягоды Фрукты",
                    0,
                    listOf(100, 200, 600),
                    Date(),
                    300500,
                    listOf(
                        SampleProduct.StaticFacets("country", "Аргентина"),
                        SampleProduct.StaticFacets("salemethod", "Упаковка"),
                        SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        SampleProduct.StaticFacets("saleregion", "Мурманская область")
                    ),
                    null,
                    SampleProduct.Payload("")
                ))
                val testEntity2 = IndexedSampleProduct(SampleProduct(
                    11,
                    SampleProduct.Category(200, "Овощи"),
                    "Помидоры красные",
                    "Россия Овощи",
                    0,
                    listOf(100, 200, 500),
                    Date(),
                    200500,
                    listOf(
                        SampleProduct.StaticFacets("country", "Украина"),
                        SampleProduct.StaticFacets("salemethod", "Поштучно"),
                        SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        SampleProduct.StaticFacets("saleregion", "Московская область")
                    ),
                    null,
                    SampleProduct.Payload("")
                ))
                val request = BulkRequest.ofCommands(
                    BulkCreate("13", testEntity),
                    BulkCreate("11", testEntity2)
                )
                whenReady(elasticDocument.bulk("product", "product").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 2
                }
            }
            "do search" {
                sleep(1000)
                @Suppress("unused")
                val searchRequest = SearchRequest(
                    query = MatchQuery.ofMatch(object : Match {
                        val id: Long = 11
                    }),
                    from = null,
                    size = null,
                    aggs = mapOf(
                        "staticStringFacetsFiltered" to FilterAggregation.of(
                            filter = NestedQuery(
                                NestedQueryBody(
                                    path = "staticFacets",
                                    query = BoolQuery.boolQuery().filter(
                                        TermQuery.ofTerm(object : Term {
                                            @JsonProperty("staticFacets.name")
                                            val staticFacetsName = "country"
                                        }),
                                        TermsQuery.ofTerms(mapOf(
                                            "staticFacets.value" to listOf("Украина")
                                        ))
                                    ).build()
                                )
                            ),
                            aggs = mapOf(
                                "staticStringFacets" to NestedAggregation.of(
                                    path = "staticFacets",
                                    aggs = mapOf(
                                        "name" to TermsAggregation.of(
                                            terms = FieldSpec("staticFacets.name"),
                                            aggs = mapOf(
                                                "value" to TermsAggregation.of(FieldSpec("staticFacets.value"))
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        "staticStringFacets" to NestedAggregation.of(
                            path = "staticFacets",
                            aggs = mapOf(
                                "name" to TermsAggregation.of(
                                    terms = FieldSpec("staticFacets.name"),
                                    aggs = mapOf(
                                        "value" to TermsAggregation.of(
                                            FieldSpec("staticFacets.value")
                                        )
                                    )
                                )
                            )
                        ),
                        "categories" to CompositeAggregation(
                            CompositeAggregation.Composite(listOf(
                                mapOf(
                                    "categoryId" to TermsAggregation(FieldSpec("category.id"))
                                ),
                                mapOf(
                                    "categoryTitle" to TermsAggregation(FieldSpec("category.title"))
                                )
                            ))
                        )
                    )
                )
                whenReady(elasticSearch.search(listOf("product"), listOf("product"))
                    .invokeT<SearchRequest, SampleProductResult>(searchRequest).toCompletableFuture()) { result ->
                    result.tamedOut shouldBe false
                    val category = result.getTyped("/aggregations/categories/after_key", SampleCategoryForProduct::class.java)
                    category.categoryId shouldBe 200
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}