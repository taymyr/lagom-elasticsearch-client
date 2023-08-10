package org.taymyr.lagom.elasticsearch.indices

import akka.Done
import akka.NotUsed
import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service.named
import com.lightbend.lagom.javadsl.api.Service.restCall
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.Method.DELETE
import com.lightbend.lagom.javadsl.api.transport.Method.GET
import com.lightbend.lagom.javadsl.api.transport.Method.HEAD
import com.lightbend.lagom.javadsl.api.transport.Method.PUT
import org.taymyr.lagom.elasticsearch.ElasticService
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import org.taymyr.lagom.elasticsearch.deser.LIST
import org.taymyr.lagom.elasticsearch.forceKF
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult
import org.taymyr.lagom.elasticsearch.indices.dsl.DeleteIndicesResult
import org.taymyr.lagom.elasticsearch.indices.dsl.IndexInfo
import kotlin.reflect.jvm.javaMethod

/**
 * Lagom service wrapper for [Elasticsearch Indices APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html).
 */
interface ElasticIndices : ElasticService {

    /**
     * Create index with name and settings.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html)
     * @param indexName name of index
     * @see IndexInfo
     */
    fun create(indexName: String): ServiceCall<CreateIndex, CreateIndexResult>

    /**
     * Delete an existing index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html)
     */
    fun delete(index: String): ServiceCall<NotUsed, DeleteIndicesResult>

    /**
     * Delete an existing indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html)
     */
    fun delete(indices: List<String>): ServiceCall<NotUsed, DeleteIndicesResult>

    /**
     * Used to check if the index exists or not.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html)
     */
    fun exists(index: String): ServiceCall<NotUsed, Done>

    /**
     * Used to check if the index (indices) exists or not.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html)
     */
    fun exists(indices: List<String>): ServiceCall<NotUsed, Done>

    /**
     * Retrieve information about index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-index.html)
     */
    fun get(index: String): ServiceCall<NotUsed, Map<String, IndexInfo>>

    /**
     * Retrieve information about one or more indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-index.html)
     */
    fun get(indices: List<String>): ServiceCall<NotUsed, Map<String, IndexInfo>>

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-indices").withCalls(
            restCall<CreateIndex, CreateIndexResult>(
                PUT, "/:indexName",
                ElasticIndices::create.javaMethod
            ),
            restCall<NotUsed, DeleteIndicesResult>(
                DELETE, "/:index",
                forceKF<ElasticIndices.(String) -> ServiceCall<*, *>>(ElasticIndices::delete).javaMethod
            ),
            restCall<NotUsed, DeleteIndicesResult>(
                DELETE, "/:indices",
                forceKF<ElasticIndices.(List<String>) -> ServiceCall<*, *>>(ElasticIndices::delete).javaMethod
            ),
            restCall<NotUsed, Done>(
                HEAD, "/:index",
                forceKF<ElasticIndices.(String) -> ServiceCall<*, *>>(ElasticIndices::exists).javaMethod
            ),
            restCall<NotUsed, Done>(
                HEAD, "/:indices",
                forceKF<ElasticIndices.(List<String>) -> ServiceCall<*, *>>(ElasticIndices::exists).javaMethod
            ),
            restCall<NotUsed, Map<String, IndexInfo>>(
                GET, "/:index",
                forceKF<ElasticIndices.(String) -> ServiceCall<*, *>>(ElasticIndices::get).javaMethod
            ),
            restCall<NotUsed, Map<String, IndexInfo>>(
                GET, "/:indices",
                forceKF<ElasticIndices.(List<String>) -> ServiceCall<*, *>>(ElasticIndices::get).javaMethod
            )
        )
            .withSerializerFactory(ElasticSerializerFactory(objectMapper()))
            .withPathParamSerializer(List::class.java, LIST)
    }
}
