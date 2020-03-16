package org.taymyr.lagom.elasticsearch.search

import org.pcollections.PSequence
import org.pcollections.TreePVector
import org.taymyr.lagom.elasticsearch.invoke
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class SearchScroller(
    val elasticSearch: ElasticSearch,
    val index: String,
    val type: String
) {
    fun <DocType, ResultType : SearchResult<out DocType>> searchAfter(
        request: SearchRequest,
        resultType: Class<ResultType>
    ): CompletionStage<List<DocType>> {
        val cf: CompletableFuture<List<DocType>> = CompletableFuture()
        val r = request.copy(
            from = 0,
            sort = if (request.sort.isNullOrEmpty()) listOf(Order.asc("_id")) else request.sort
        )
        searchAfter(r, resultType, cf, TreePVector.empty())
        return cf
    }

    private fun <DocType, ResultType : SearchResult<out DocType>> searchAfter(
        request: SearchRequest,
        resultType: Class<ResultType>,
        cf: CompletableFuture<List<DocType>>,
        accum: PSequence<DocType>
    ) {
        elasticSearch.search(index, type).invoke(request, resultType).whenComplete { result, throwable ->
            if (result != null) {
                if (result.sources.isNullOrEmpty()) {
                    cf.complete(accum)
                } else {
                    val r = request.copy(searchAfter = result.hits.hits.last().sort, from = 0)
                    searchAfter(r, resultType, cf, accum.plusAll(result.sources))
                }
            } else {
                cf.completeExceptionally(throwable)
            }
        }
    }
}