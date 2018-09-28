package org.taymyr.lagom.elasticsearch.client

import akka.Done
import akka.NotUsed
import com.lightbend.lagom.javadsl.api.Service
import com.lightbend.lagom.javadsl.api.ServiceCall
import org.taymyr.lagom.elasticsearch.client.dsl.QueryRoot
import org.taymyr.lagom.elasticsearch.client.serialize.JsonBytes

interface ElasticSearchKt : Service {
    fun updateIndex(index: String, type: String, id: String): ServiceCall<JsonBytes, Done>

    fun search(index: String, type: String): ServiceCall<QueryRoot, JsonBytes>

    fun deleteById(index: String, type: String, id: String): ServiceCall<NotUsed, Done>
}
