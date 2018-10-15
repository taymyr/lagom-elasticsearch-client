package org.taymyr.lagom.elasticsearch.search

import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.AutocompleteFilter
import org.taymyr.lagom.elasticsearch.IndexedSampleCategory
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleCategory
import org.taymyr.lagom.elasticsearch.SampleCategoryResult
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentResult
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CustomAnalyzer
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Ids
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery
import java.lang.Thread.sleep

class ElasticSearchIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "ElasticSearch" should {
            "put documents" {
                repeat(10) {
                    whenReady(elasticDocument.indexWithId("test", "sample", it.toString())
                        .invoke(SampleDocument("user-$it", "message-$it")).toCompletableFuture()) { result ->
                        result.index shouldBe "test"
                        result.type shouldBe "sample"
                    }
                }
            }
            "search a document by id" {
                sleep(1000)
                val searchRequest = SearchRequest(IdsQuery(Ids(values = listOf("2"))))
                val check = { result: SampleDocumentResult ->
                    result.tamedOut shouldBe false
                    result.hits.run {
                        total shouldBe 1
                        hits shouldHaveSize 1
                        hits[0].score shouldBe 1.0
                        hits[0].source.user shouldBe "user-2"
                        hits[0].source.message shouldBe "message-2"
                    }
                }
                whenReady(elasticSearch.search(listOf("test"), listOf("sample")).invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture(), check)
                whenReady(elasticSearch.search(listOf("test")).invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture(), check)
                whenReady(elasticSearch.search("test", "sample").invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture(), check)
                whenReady(elasticSearch.search("test").invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture(), check)
                whenReady(elasticSearch.search().invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture(), check)
            }
        }
        "Advanced ElasticSearch" should {
            "create index with analyzer" {
                val request = CreateIndex(
                    CreateIndex.Settings(1, 1, CreateIndex.Analysis(
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
                            "name" to MappingProperty(DataType.TEXT, "autocomplete"),
                            "title" to MappingProperty.OBJECT,
                            "technicalName" to MappingProperty.TEXT,
                            "attachAllowed" to MappingProperty.BOOLEAN
                        ))
                    )
                )
                whenReady(elasticIndices.create("category").invoke(request).toCompletableFuture()) { result ->
                    result.acknowledged shouldBe true
                    result.shardsAcknowledged shouldBe true
                    result.index shouldBe "category"
                }
            }
            "successful add test category" {
                val request = BulkRequest.ofCommands(BulkCreate("1", IndexedSampleCategory(SampleCategory(
                    1,
                    listOf("test"),
                    null,
                    null,
                    true
                ))))
                whenReady(elasticDocument.bulk("category", "some_type").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 1
                    val item = result.items[0]
                    item.status shouldBe 201
                    item.result shouldBe "created"
                    item.error shouldBe null
                    item.index shouldBe "category"
                    item.type shouldBe "some_type"
                    item.id shouldBe "1"
                }
            }
            "successful search document using autocomplete filter" {
                sleep(1000)
                val searchRequest = SearchRequest(
                    MatchQuery(object : Match {
                        val name = "te"
                    })
                )
                whenReady(elasticSearch.search(listOf("category"), listOf("some_type"))
                    .invokeT<SearchRequest, SampleCategoryResult>(searchRequest).toCompletableFuture()) { result ->
                    result.tamedOut shouldBe false
                    result.hits.run {
                        total shouldBe 1
                        hits shouldHaveSize 1
                        hits[0].source.name shouldNotBe null
                        hits[0].source.name!![0] shouldBe "test"
                    }
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}