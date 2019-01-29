package org.taymyr.lagom.elasticsearch.search

import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.MessageMatchPhrase
import org.taymyr.lagom.elasticsearch.MessageMatchPhrasePrefix
import org.taymyr.lagom.elasticsearch.MessagePrefix
import org.taymyr.lagom.elasticsearch.MessageRegexp
import org.taymyr.lagom.elasticsearch.MessageWildcard
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentResult
import org.taymyr.lagom.elasticsearch.UserKeywordTerm
import org.taymyr.lagom.elasticsearch.UserMatchPhrase
import org.taymyr.lagom.elasticsearch.UserMatchPhrasePrefix
import org.taymyr.lagom.elasticsearch.UserPrefix
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.Companion.KEYWORD
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.Companion.TEXT
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery.Companion.boolQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrasePrefixQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhraseQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.PrefixQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.RegexpQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.WildcardQuery
import java.lang.Thread.sleep
import java.time.LocalDateTime.now
import java.time.temporal.ChronoUnit.DAYS
import java.util.UUID

/**
 * Testing full-test searching, including:
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhraseQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrasePrefixQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.RegexpQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.WildcardQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.PrefixQuery]
 */
class ElasticSearchFullTextIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "ElasticSearch" should {
            val indexName = "full-text-test"
            val typeName = "samples"
            "successfully create the index named full-text-test" {
                val request = CreateIndex(
                    CreateIndex.Settings(1, 1),
                    mapOf(
                        typeName to Mapping(mapOf(
                            "user" to KEYWORD,
                            "message" to TEXT
                        ))
                    )
                )
                whenReady(elasticIndices.create(indexName).invoke(request).toCompletableFuture()) {
                    it.acknowledged shouldBe true
                    it.shardsAcknowledged shouldBe true
                    it.index shouldBe indexName
                }
            }
            "successfully index 20 documents with random keyword 'users' and text 'message'" {
                repeat(20) {
                    val uuid = "$it" + UUID.randomUUID()
                    val symbol = if (it % 10 == 3) uuid else ""
                    val doc = SampleDocument(
                        user = "fullTextUser$uuid",
                        message = "random$symbol generated message $uuid for full text search",
                        creationDate = now().truncatedTo(DAYS)
                    )
                    whenReady(elasticDocument.indexWithId(indexName, typeName, uuid).invoke(doc).toCompletableFuture()) { result ->
                        result.index shouldBe indexName
                        result.type shouldBe typeName
                    }
                }
                sleep(1000)
            }
            "find none documents with keyword 'user' term 'fullTextUser'" {
                val request = SearchRequest(
                    query = boolQuery().must(TermQuery(UserKeywordTerm("fullTextUser"))).build()
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request).toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 0
                }
            }
            "find none documents with keyword 'user' match-phrase 'fullTextUser'" {
                val request = SearchRequest(
                    query = boolQuery().filter(MatchPhraseQuery(UserMatchPhrase("fullTextUser"))).build()
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 0
                }
            }
            "successfully find all documents with keyword 'user' match-phrase-prefix 'fullTextUser'" {
                val request = SearchRequest(
                    query = boolQuery().filter(MatchPhrasePrefixQuery(UserMatchPhrasePrefix("fullTextUser"))).build(),
                    size = 25
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 20
                }
            }
            "successfully find all documents with keyword 'user' prefix 'full'" {
                val request = SearchRequest(
                    query = boolQuery().filter(PrefixQuery(UserPrefix("full"))).build(),
                    size = 25
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 20
                }
            }
            "successfully find all documents with text 'message' match-phrase 'generated'" {
                val request = SearchRequest(
                    query = boolQuery().must(MatchPhraseQuery(MessageMatchPhrase("generated"))).build(),
                    size = 25
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 20
                }
            }
            "find none documents with text 'message' match-phrase 'random'" {
                val request = SearchRequest(
                    query = boolQuery().filter(MatchPhraseQuery(MessageMatchPhrase("random"))).build(),
                    size = 25
                )
                // Two record with `random${simbol}` message should not be found
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 18
                }
            }
            "successfully find all documents with text 'message' match-phrase-prefix 'random'" {
                val request = SearchRequest(
                    query = boolQuery().filter(MatchPhrasePrefixQuery(MessageMatchPhrasePrefix("random"))).build(),
                    size = 25
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 20
                }
            }
            "successfully find all documents text 'message' prefix 'random' that is for terms" {
                val request = SearchRequest(
                    query = boolQuery().filter(PrefixQuery(MessagePrefix("random"))).build(),
                    size = 25
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 20
                }
            }
            "successfully find all documents text 'message' regexp 'random.+' that is for terms" {
                val request = SearchRequest(
                    query = boolQuery().filter(RegexpQuery(MessageRegexp("random.+"))).build()
                )
                // Two record with `random${simbol}` message should be found
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 2
                }
            }
            "successfully find all documents text 'message' wildcard 'random?*' that is for terms" {
                val request = SearchRequest(
                    query = boolQuery().filter(WildcardQuery(MessageWildcard("random?*"))).build()
                )
                whenReady(elasticSearch.search(indexName).invokeT<SearchRequest, SampleDocumentResult>(request)
                    .toCompletableFuture()) { result ->
                    result.hits.hits shouldHaveSize 2
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}