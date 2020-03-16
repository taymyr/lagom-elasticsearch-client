@file:JvmName("SearchUtils")

package org.taymyr.lagom.elasticsearch.search

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import org.taymyr.lagom.elasticsearch.invoke
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order.Companion.asc
import java.util.concurrent.CompletionStage

/**
 * Finds all matched documents using [Search After](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-search-after)
 */
fun <DocType, ResultType : SearchResult<out DocType>> ElasticSearch.findAll(
    index: String,
    type: String,
    request: SearchRequest,
    resultType: Class<ResultType>
): CompletionStage<List<DocType>> = GlobalScope.future {
    val l: MutableList<List<DocType>> = ArrayList()
    findAllFlow(index, type, request, resultType).map { it.sources }.toList(l).flatten()
}

/**
 * Returns a flow of results produced with [Search After](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html#request-body-search-search-after)
 */
suspend fun <ResultType : SearchResult<*>> ElasticSearch.findAllFlow(
    index: String,
    type: String,
    request: SearchRequest,
    resultType: Class<ResultType>
): Flow<ResultType> = flow {
    var r = request.copy(
        from = 0,
        sort = if (request.sort.isNullOrEmpty()) request.sort else listOf(asc("_id"))
    )
    var result = search(index, type).invoke(r, resultType).await()
    do {
        emit(result)
        r = r.copy(searchAfter = result.hits.hits.last().sort, from = 0)
        result = search(index, type).invoke(r, resultType).await()
    } while (!result.sources.isNullOrEmpty())
}
