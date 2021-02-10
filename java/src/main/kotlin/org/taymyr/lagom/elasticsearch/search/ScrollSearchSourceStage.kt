package org.taymyr.lagom.elasticsearch.search

import akka.stream.Attributes
import akka.stream.Outlet
import akka.stream.SourceShape
import akka.stream.javadsl.Source
import akka.stream.stage.AbstractOutHandler
import akka.stream.stage.GraphStage
import akka.stream.stage.GraphStageLogic
import org.taymyr.lagom.elasticsearch.invoke
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order
import java.util.LinkedList
import java.util.Queue

/**
 * The [GraphStage] stage of scrolling search results.
 */
class ScrollSearchSourceStage<T : SearchResult<*>>(
    val index: String,
    val type: String,
    val request: SearchRequest,
    val resultClass: Class<T>,
    val client: ElasticSearch
) : GraphStage<SourceShape<T>>() {
    private val out = Outlet.create<T>("ScrollSearchSourceStage.out")
    private val shape = SourceShape(out)

    override fun createLogic(inheritedAttributes: Attributes?): GraphStageLogic =
        object : GraphStageLogic(shape) {
            private val queue: Queue<T> = LinkedList()
            private val searchCompleteCallback = createAsyncCallback(::handleSearchComplete)
            private val searchFailureCallback = createAsyncCallback(::handleSearchFailure)
            private lateinit var nextRequest: SearchRequest

            init {
                setHandler(out, object : AbstractOutHandler() {
                    override fun onPull() {
                        if (!queue.isEmpty()) {
                            push(out, queue.poll())
                        }
                    }
                })
            }

            private fun searchAfter(r: SearchRequest) {
                client.search(index, type).invoke(r, resultClass).whenComplete { result, throwable ->
                    if (throwable != null) {
                        searchFailureCallback.invoke(throwable)
                    } else {
                        searchCompleteCallback.invoke(result)
                    }
                }
            }

            override fun preStart() {
                nextRequest = request.copy(
                    from = 0,
                    sort = if (request.sort.isNullOrEmpty()) listOf(Order.asc("_id")) else request.sort
                )
                searchAfter(nextRequest)
            }

            fun handleSearchComplete(result: T) {
                if (!result.sources.isNullOrEmpty()) {
                    if (isAvailable(out)) {
                        push(out, result)
                    } else {
                        queue.offer(result)
                    }
                    nextRequest = nextRequest.copy(
                        searchAfter = result.hits.hits.last().sort,
                        from = 0
                    )
                    searchAfter(nextRequest)
                } else {
                    emitMultiple(out, queue.iterator()) { completeStage() }
                }
            }

            fun handleSearchFailure(throwable: Throwable) {
                failStage(throwable)
            }
        }

    override fun shape(): SourceShape<T> = shape

    companion object {
        /**
         * Creates a [Source] of scrolling search results.
         */
        @JvmStatic
        fun <T : SearchResult<*>> scrollSearchSource(index: String, docType: String, request: SearchRequest, resultClass: Class<T>, client: ElasticSearch) =
            Source.fromGraph(ScrollSearchSourceStage(index, docType, request, resultClass, client))
    }
}
