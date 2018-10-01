package org.taymyr.lagom.elasticsearch.test

import akka.Done
import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import org.taymyr.lagom.elasticsearch.ElasticSearch
import org.taymyr.lagom.elasticsearch.ElasticSearchAdministration
import org.taymyr.lagom.elasticsearch.dsl.search.QueryRoot
import java.net.URI
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ElasticSearchLagomClientTestIT : AbstractEmbeddedElastic() {

    companion object {

        private var elasticSearch: ElasticSearch? = null

        private var elasticRepositoryC1: ElasticRepositoryC1? = null

        private var elasticSearchAdministration: ElasticSearchAdministration? = null

        private var lagomClientFactory: LagomClientFactory? = null

        @BeforeClass
        @JvmStatic fun beforeClass() {
            lagomClientFactory = LagomClientFactory.create("elastic-search", LagomClientFactory::class.java.classLoader)
            // Our not need elasticSearch client if we can't to create elasticSearch instance :)
            embeddedElastic?.let { ee ->
                lagomClientFactory?.let { lcf ->
                    ee.start()
                    elasticSearch = lcf.createClient(ElasticSearch::class.java, URI.create("http://localhost:9250"))
                    elasticSearch?.let { it ->
                        run {
                            elasticRepositoryC1 = ElasticRepositoryC1(it)
                            elasticSearchAdministration = ElasticSearchAdministration(it, "c1", "all")
                        }
                    }
                }
            }
        }

        @AfterClass
        @JvmStatic fun afterClass() {
            elasticRepositoryC1 = null
            elasticSearch = null
            embeddedElastic?.stop()
            lagomClientFactory?.close()
        }
    }

    @Test
    fun `stage1 elasticSearch is not null`() {
        elasticSearch!!
        embeddedElastic!!
        elasticRepositoryC1!!
        elasticSearchAdministration!!
    }

    @Test(expected = ExecutionException::class)
    fun `stage2 use elasticSearch`() {
        elasticSearch!!.search("qwe", "qwe").invoke(QueryRoot(null, null, null, null)).toCompletableFuture().get(2, TimeUnit.SECONDS)
    }

    @Test
    fun `stage3 auto initialize settings`() {
        // Will be load and apply settings from: elasticsearch/settings.json
        val result = elasticSearchAdministration!!.initializeSettings().toCompletableFuture().get(2, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(Done.done())
    }
}
