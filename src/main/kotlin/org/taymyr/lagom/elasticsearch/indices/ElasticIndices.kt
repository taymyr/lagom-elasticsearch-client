package org.taymyr.lagom.elasticsearch.indices

import akka.Done
import akka.NotUsed
import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service
import com.lightbend.lagom.javadsl.api.Service.named
import com.lightbend.lagom.javadsl.api.Service.restCall
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.Method.DELETE
import com.lightbend.lagom.javadsl.api.transport.Method.GET
import com.lightbend.lagom.javadsl.api.transport.Method.HEAD
import com.lightbend.lagom.javadsl.api.transport.Method.PUT
import org.taymyr.lagom.elasticsearch.deser.ElasticJacksonSerializerFactory
import org.taymyr.lagom.elasticsearch.deser.ListStringPathParamSerializer
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult
import org.taymyr.lagom.elasticsearch.indices.dsl.DeleteIndicesResult
import org.taymyr.lagom.elasticsearch.indices.dsl.IndexInfo
import kotlin.reflect.jvm.javaMethod

/**
 * Lagom service wrapper for [Elasticsearch Indices APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html)
 * @author Sergey Morgunov
 */
interface ElasticIndices : Service {

    /**
     * Create index with name and settings.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-create-index.html)
     * @param indexName name of index
     * @see IndexInfo
     */
    fun create(indexName: String): ServiceCall<CreateIndex, CreateIndexResult>

    /**
     * Delete an existing indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-delete-index.html)
     */
    fun delete(indices: List<String>): ServiceCall<NotUsed, DeleteIndicesResult>

    /**
     * Used to check if the index (indices) exists or not.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html)
     */
    fun exists(indices: List<String>): ServiceCall<NotUsed, Done>

    /**
     * Retrieve information about one or more indices.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-get-index.html)
     */
    fun get(indices: List<String>): ServiceCall<NotUsed, Map<String, IndexInfo>>

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-indices").withCalls(
            restCall<CreateIndex, CreateIndexResult>(PUT, "/:indexName", ElasticIndices::create.javaMethod),
            restCall<NotUsed, DeleteIndicesResult>(DELETE, "/:indices", ElasticIndices::delete.javaMethod),
            restCall<NotUsed, Done>(HEAD, "/:indices", ElasticIndices::exists.javaMethod),
            restCall<NotUsed, Map<String, IndexInfo>>(GET, "/:indices", ElasticIndices::get.javaMethod)
        )
            .withSerializerFactory(ElasticJacksonSerializerFactory)
            .withPathParamSerializer(List::class.java, ListStringPathParamSerializer)
    }
}