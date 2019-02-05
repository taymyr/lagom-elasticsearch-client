package org.taymyr.lagom.elasticsearch.document

import akka.Done
import akka.NotUsed
import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service.named
import com.lightbend.lagom.javadsl.api.Service.restCall
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.Method.DELETE
import com.lightbend.lagom.javadsl.api.transport.Method.GET
import com.lightbend.lagom.javadsl.api.transport.Method.HEAD
import com.lightbend.lagom.javadsl.api.transport.Method.POST
import com.lightbend.lagom.javadsl.api.transport.Method.PUT
import org.taymyr.lagom.elasticsearch.ElasticService
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import org.taymyr.lagom.elasticsearch.document.dsl.IndexDocumentResult
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult
import org.taymyr.lagom.elasticsearch.document.dsl.delete.DeleteResult
import org.taymyr.lagom.elasticsearch.document.dsl.update.UpdateResult
import kotlin.reflect.jvm.javaMethod

/**
 * Lagom service wrapper for [Elasticsearch Document APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html)
 */
interface ElasticDocument : ElasticService {

    /**
     * Add document to index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html)
     */
    fun indexWithId(index: String, type: String, id: String): ServiceCall<ByteString, IndexDocumentResult>

    /**
     * Retrieve document with meta from index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html)
     */
    fun get(index: String, type: String, id: String): ServiceCall<NotUsed, ByteString>

    /**
     * Retrieve document (only source) from index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#_source)
     */
    fun getSource(index: String, type: String, id: String): ServiceCall<NotUsed, ByteString>

    /**
     * Check the document exists on index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#docs-get)
     */
    fun exists(index: String, type: String, id: String): ServiceCall<NotUsed, Done>

    /**
     * Check the document source exists on index.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-get.html#_source)
     */
    fun existsSource(index: String, type: String, id: String): ServiceCall<NotUsed, Done>

    /**
     * Executing bulk requests.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)
     */
    fun bulk(index: String, type: String): ServiceCall<BulkRequest, BulkResult>

    /**
     * Executes update document requests.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html)
     */
    fun update(index: String, type: String, id: String): ServiceCall<ByteString, UpdateResult>

    /**
     * Executes delete document requests.
     * See also [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-delete.html)
     */
    fun delete(index: String, type: String, id: String): ServiceCall<NotUsed, DeleteResult>

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-document").withCalls(
            restCall<ByteString, IndexDocumentResult>(PUT, "/:index/:type/:id", ElasticDocument::indexWithId.javaMethod),
            restCall<NotUsed, ByteString>(GET, "/:index/:type/:id", ElasticDocument::get.javaMethod),
            restCall<NotUsed, Done>(HEAD, "/:index/:type/:id", ElasticDocument::exists.javaMethod),
            restCall<NotUsed, ByteString>(GET, "/:index/:type/:id/_source", ElasticDocument::getSource.javaMethod),
            restCall<NotUsed, Done>(HEAD, "/:index/:type/:id/_source", ElasticDocument::existsSource.javaMethod),
            restCall<BulkRequest, BulkResult>(POST, "/:index/:type/_bulk", ElasticDocument::bulk.javaMethod),
            restCall<ByteString, UpdateResult>(POST, "/:index/:type/:id/_update", ElasticDocument::update.javaMethod),
            restCall<NotUsed, DeleteResult>(DELETE, "/:index/:type/:id", ElasticDocument::delete.javaMethod)
        )
            .withSerializerFactory(ElasticSerializerFactory(objectMapper()))
    }
}