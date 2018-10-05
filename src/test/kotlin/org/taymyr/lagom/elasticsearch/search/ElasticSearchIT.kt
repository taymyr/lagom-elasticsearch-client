package org.taymyr.lagom.elasticsearch.search

import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentResult
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.Ids
import org.taymyr.lagom.elasticsearch.search.dsl.query.IdsQuery
import java.lang.Thread.sleep

class ElasticSearchIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "ElasticSearch" should {
            "successfully put documents" {
                repeat(10) {
                    whenReady(elasticDocument.indexWithId("test", "sample", it.toString())
                        .invoke(SampleDocument("user-$it", "message-$it")).toCompletableFuture()) { result ->
                        result.index shouldBe "test"
                        result.type shouldBe "sample"
                    }
                }
            }
            "successfully search a document by id" {
                sleep(1000)
                val searchRequest = SearchRequest(IdsQuery(Ids(values = listOf("2"))))
                whenReady(elasticSearch.search(listOf("test"), listOf("sample")).invokeT<SearchRequest, SampleDocumentResult>(searchRequest).toCompletableFuture()) {
                    it.tamedOut shouldBe false
                    it.hits.run {
                        total shouldBe 1
                        hits shouldHaveSize 1
                        hits[0].score shouldBe 1.0
                        hits[0].source.user shouldBe "user-2"
                        hits[0].source.message shouldBe "message-2"
                    }
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}