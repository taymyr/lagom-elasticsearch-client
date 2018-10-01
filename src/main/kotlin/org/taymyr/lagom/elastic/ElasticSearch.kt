package org.taymyr.lagom.elastic

import akka.Done
import com.lightbend.lagom.javadsl.api.Descriptor
import com.lightbend.lagom.javadsl.api.Service
import com.lightbend.lagom.javadsl.api.Service.named
import com.lightbend.lagom.javadsl.api.Service.restCall
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.api.transport.Method.POST
import kotlin.reflect.jvm.javaMethod

/**
 * @author Sergey Morgunov
 */
interface ElasticSearch : Service {

    fun updateIndex(index: String, type: String, id: String): ServiceCall<String, Done>

    @JvmDefault
    override fun descriptor(): Descriptor {
        return named("elastic-search")
            .withCalls(
                restCall<String, Done>(POST, "/:index/:type/:id/_update", ElasticSearch::updateIndex.javaMethod)
            )
    }
}