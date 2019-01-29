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
import org.taymyr.lagom.elasticsearch.MessageKeywordTerm
import org.taymyr.lagom.elasticsearch.SampleCategory
import org.taymyr.lagom.elasticsearch.SampleCategoryResult
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentResult
import org.taymyr.lagom.elasticsearch.UserKeywordTerm
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CustomAnalyzer
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MultiMatchQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Ids
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery
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
                        "some_type" to Mapping(mapOf(
                            "id" to MappingProperty.LONG,
                            "name" to MappingProperty(type = DataType.TEXT, analyzer = "autocomplete"),
                            "title" to MappingProperty.OBJECT,
                            "technicalName" to MappingProperty.TEXT,
                            "fullText" to MappingProperty.TEXT,
                            "fullTextBoosted" to MappingProperty.TEXT,
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
                val request = BulkRequest.ofCommands(
                    BulkCreate("1",
                        IndexedSampleCategory(
                            SampleCategory(
                                1,
                                listOf("test1"),
                                null,
                                null,
                                true,
                                "Овощи",
                                "Огурцы"
                            )
                        )
                    ),
                    BulkCreate("2",
                        IndexedSampleCategory(
                            SampleCategory(
                                1,
                                listOf("test1"),
                                null,
                                null,
                                true,
                                "Овощи",
                                "Капуста"
                            )
                        )
                    )
                )
                whenReady(elasticDocument.bulk("category", "some_type").invoke(request).toCompletableFuture()) { result ->
                    result shouldBe beInstanceOf(BulkResult::class)
                    result.errors shouldBe false
                    result.items shouldHaveSize 2
                    result.items[0].run {
                        this.status shouldBe 201
                        this.result shouldBe "created"
                        this.error shouldBe null
                        this.index shouldBe "category"
                        this.type shouldBe "some_type"
                        this.id shouldBe "1"
                    }
                }
            }
            "successful search document using autocomplete filter" {
                sleep(1000)
                val searchRequest = SearchRequest(
                    MatchQuery(object : Match {
                        val name = "test1"
                    })
                )
                whenReady(elasticSearch.search(listOf("category"), listOf("some_type"))
                    .invokeT<SearchRequest, SampleCategoryResult>(searchRequest).toCompletableFuture()) { result ->
                    result.tamedOut shouldBe false
                    result.hits.run {
                        total shouldBe 2
                        hits shouldHaveSize 2
                        hits[0].source.name shouldNotBe null
                        hits[0].source.name!![0] shouldBe "test1"
                    }
                }
            }
            "successful search document using multi-match" {
                sleep(1000)
                val searchRequest = SearchRequest(
                    MultiMatchQuery.of(
                        "Огурцовые овощи",
                        mapOf(
                            Pair("fullTextBoosted", 10),
                            Pair("fullText", 3)
                        )
                    )
                )
                whenReady(elasticSearch.search(listOf("category"), listOf("some_type"))
                    .invokeT<SearchRequest, SampleCategoryResult>(searchRequest).toCompletableFuture()) { result ->
                    result.tamedOut shouldBe false
                    result.hits.run {
                        total shouldBe 2
                        hits shouldHaveSize 2
                        hits[0].source.name shouldNotBe null
                        hits[0].source.name!![0] shouldBe "test1"
                    }
                }
            }
        }
        "Request Body Search with Query DSL" should {
            "successfully perform BoolQuery search" {
                val userTerm = UserKeywordTerm("user-9")
                val messageTerm = MessageKeywordTerm("message-9")
                val request = SearchRequest(
                    BoolQuery.boolQuery()
                        .must(listOf(TermQuery.ofTerm(userTerm), TermQuery.ofTerm(messageTerm)))
                        .build(),
                    0, 100
                )
                whenReady(elasticSearch.search(listOf("test"), listOf("sample")).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) {
                    val found = it.hits.hits.filter {
                        h -> h.source.user == userTerm.user && h.source.message == messageTerm.message
                    }
                    found shouldHaveSize 1
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}