package org.taymyr.lagom.elasticsearch

import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
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
import org.taymyr.lagom.elasticsearch.search.dsl.query.Fuzzy
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.CompletionSuggest
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.CompletionSuggest.Completion
import java.lang.Thread.sleep
import java.util.Date

class SuggestsIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "Completion suggest" should {
            "successfully create new index" {
                val request = CreateIndex(
                    Settings(1, 1),
                    mapOf(
                        "product" to Mapping(mapOf(
                            "id" to MappingProperty.LONG,
                            "category.id" to MappingProperty.LONG,
                            "category.title" to MappingProperty.KEYWORD,
                            "fullTextBoosted" to MappingProperty(type = DataType.TEXT, analyzer = "russian"),
                            "fullText" to MappingProperty(type = DataType.TEXT, analyzer = "russian"),
                            "sellerId" to MappingProperty.LONG,
                            "categoryIds" to MappingProperty.LONG,
                            "updateDate" to MappingProperty.DATE,
                            "basePrice" to MappingProperty.INTEGER,
                            "suggest" to MappingProperty(type = "completion"),
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
                    id = 13,
                    category = SampleProduct.Category(100, "Ягоды"),
                    fullTextBoosted = "Клубника вкусная",
                    fullText = "Россия Ягоды Фрукты",
                    sellerId = 0,
                    categoryIds = listOf(100, 200, 600),
                    updateDate = Date(),
                    basePrice = 300500,
                    staticFacets = listOf(
                        SampleProduct.StaticFacets("country", "Аргентина"),
                        SampleProduct.StaticFacets("salemethod", "Упаковка"),
                        SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        SampleProduct.StaticFacets("saleregion", "Мурманская область")
                    ),
                    suggest = listOf("Клубника вкусная"),
                    payload = SampleProduct.Payload("")
                ))
                val testEntity2 = IndexedSampleProduct(SampleProduct(
                    id = 11,
                    category = SampleProduct.Category(200, "Овощи"),
                    fullTextBoosted = "Помидоры красные",
                    fullText = "Россия Овощи",
                    sellerId = 0,
                    categoryIds = listOf(100, 200, 500),
                    updateDate = Date(),
                    basePrice = 200500,
                    staticFacets = listOf(
                        SampleProduct.StaticFacets("country", "Украина"),
                        SampleProduct.StaticFacets("salemethod", "Поштучно"),
                        SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        SampleProduct.StaticFacets("saleregion", "Московская область")
                    ),
                    suggest = listOf("Помидоры красные"),
                    payload = SampleProduct.Payload("")
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
                    suggest = mapOf(
                        "mySuggest" to CompletionSuggest("помадор", Completion(
                            field = "suggest",
                            fuzzy = Fuzzy.auto()
                        ))
                    )
                )
                whenReady(elasticSearch.search(listOf("product"), listOf("product"))
                    .invokeT<SearchRequest, SampleProductResult>(searchRequest).toCompletableFuture()) { result ->
                    result.tamedOut shouldBe false
                    val suggest = result.suggest?.get("mySuggest")
                    suggest shouldNotBe null
                    suggest!![0].options.get(0).text shouldBe "Помидоры красные"
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}