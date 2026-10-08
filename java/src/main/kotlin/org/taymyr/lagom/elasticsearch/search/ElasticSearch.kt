package org.taymyr.lagom.elasticsearch.search

import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service.named
import com.lightbend.lagom.javadsl.api.Service.restCall
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.Method.GET
import com.lightbend.lagom.javadsl.api.transport.Method.POST
import org.taymyr.lagom.elasticsearch.ElasticService
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import org.taymyr.lagom.elasticsearch.deser.LIST
import org.taymyr.lagom.elasticsearch.forceKF
import org.taymyr.lagom.elasticsearch.search.dsl.CountResult
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchResult
import kotlin.reflect.jvm.javaMethod

/**
 * Lagom service wrapper for [Elasticsearch Search APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html).
 */
interface ElasticSearch : ElasticService {

    /**
     * Search documents across all types within an index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html#search-multi-index-type)
     */
    fun search(index: String): ServiceCall<SearchRequest, ByteString>

    /**
     * Search documents across all types within an index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html#search-multi-index-type)
     */
    fun search(indices: List<String>): ServiceCall<SearchRequest, ByteString>

    /**
     * Search documents across all types within an index, and across all indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-search.html#search-multi-index-type)
     */
    fun search(): ServiceCall<SearchRequest, ByteString>

    /**
     * Search documents across all types within an index, and across all indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-count.html)
     */
    fun count(index: String): ServiceCall<SearchRequest, CountResult>

    /**
     * Executes several searches with a single request. Header of every search must contain an index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/search-multi-search.html)
     */
    fun msearch(): ServiceCall<MultiSearchRequest, MultiSearchResult>

    /**
     * Performs multiple search operations within a single request.
     * The index is used for the search unless a specific index is specified in the request header.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/search-multi-search.html)
     */
    fun msearch(index: String): ServiceCall<MultiSearchRequest, MultiSearchResult>

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-search").withCalls(
            restCall<SearchRequest, ByteString>(
                GET, "/:indices/_search",
                forceKF<ElasticSearch.(List<String>) -> ServiceCall<*, *>>(ElasticSearch::search).javaMethod
            ),
            restCall<SearchRequest, ByteString>(
                GET, "/:index/_search",
                forceKF<ElasticSearch.(String) -> ServiceCall<*, *>>(ElasticSearch::search).javaMethod
            ),
            restCall<SearchRequest, ByteString>(
                GET, "/_search",
                forceKF<ElasticSearch.() -> ServiceCall<*, *>>(ElasticSearch::search).javaMethod
            ),
            restCall<SearchRequest, CountResult>(
                GET, "/:index/_count",
                ElasticSearch::count.javaMethod
            ),
            restCall<MultiSearchRequest, MultiSearchResult>(
                POST, "/:index/_msearch",
                forceKF<ElasticSearch.(String) -> ServiceCall<*, *>>(ElasticSearch::msearch).javaMethod
            ),
            restCall<MultiSearchRequest, MultiSearchResult>(
                POST, "/_msearch",
                forceKF<ElasticSearch.() -> ServiceCall<*, *>>(ElasticSearch::msearch).javaMethod
            )
        )
            .withPathParamSerializer(List::class.java, LIST)
            .withSerializerFactory(ElasticSerializerFactory(objectMapper()))
    }
}
