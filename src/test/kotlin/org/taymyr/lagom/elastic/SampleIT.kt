package org.taymyr.lagom.elastic

import akka.Done
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.whenReady
import java.net.URI
import java.util.Optional.empty
import java.util.Optional.of
import java.util.concurrent.CompletableFuture

/**
 * @author Sergey Morgunov
 */
class SampleIT : StringSpec() {

    override fun listeners(): List<TestListener> = listOf(SampleIT.Companion)

    init {
        "test update index" {
            val a: CompletableFuture<Done> = elasticSearch!!.updateIndex("test", "doc", "1")
                .handleRequestHeader { headers -> headers.withProtocol(MessageProtocol(of("application/json"), of("utf-8"), empty())) }
                .invoke(
                """{
                        "doc" : {
                            "name" : "new_name"
                        },
                        "doc_as_upsert" : true
                    }""".trimMargin()
            ).toCompletableFuture()
            whenReady(a) {
                it shouldBe Done.done()
            }
        }
    }

    companion object : TestListener {

        private var clientFactory: LagomClientFactory? = null
        var elasticSearch: ElasticSearch? = null

        override fun beforeSpec(description: Description, spec: Spec) {
            clientFactory = LagomClientFactory.create(
                "elastic-search",
                LagomClientFactory::class.java.classLoader
            )
            elasticSearch = clientFactory?.createClient(ElasticSearch::class.java, URI("http://localhost:9200"))
        }

        override fun afterSpec(description: Description, spec: Spec) {
            clientFactory?.close()
        }
    }
}