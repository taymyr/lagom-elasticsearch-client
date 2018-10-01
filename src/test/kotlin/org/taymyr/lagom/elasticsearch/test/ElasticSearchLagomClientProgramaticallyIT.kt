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
import org.taymyr.lagom.elasticsearch.dsl.mapping.ElasticSearchMapping
import org.taymyr.lagom.elasticsearch.dsl.mapping.MappingProperties
import org.taymyr.lagom.elasticsearch.dsl.mapping.MappingProperty
import org.taymyr.lagom.elasticsearch.dsl.mapping.MappingTypes
import org.taymyr.lagom.elasticsearch.dsl.search.QueryRoot
import org.taymyr.lagom.elasticsearch.dsl.settings.Analyzer
import org.taymyr.lagom.elasticsearch.dsl.settings.ElasticSearchIndexSettings
import java.net.URI
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ElasticSearchLagomClientProgramaticallyIT : AbstractEmbeddedElastic() {

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

    private val timeOut: Long = 5

    @Test
    fun `stage1 elasticSearch is not null`() {
        elasticSearch!!
        embeddedElastic!!
        elasticRepositoryC1!!
        elasticSearchAdministration!!
    }

    @Test(expected = ExecutionException::class)
    fun `stage2 use elasticSearch`() {
        elasticSearch!!.search("qwe", "qwe").invoke(QueryRoot(null, null, null, null))
            .toCompletableFuture().get(timeOut, TimeUnit.SECONDS)
    }

    @Test
    fun `stage3 set settings for index 'c1' programatically`() {
        val settingsAdministration = ElasticSearchIndexSettings(
            1,
            ElasticSearchIndexSettings.Analysis(
                filter = mapOf(
                    "autocomplete_filter" to AutocompleteFilter(
                        "edge_ngram",
                        1,
                        20
                    )
                ),
                analyzer = mapOf(
                    "autocomplete" to Analyzer(
                        "custom",
                        "standard",
                        listOf(
                            "lowercase",
                            "autocomplete_filter"
                        )
                    )
                )
            )
        )
        val result = elasticSearchAdministration!!.createIndexWithSettings(settingsAdministration).toCompletableFuture().get()
        assertThat(result).isEqualTo(Done.done())
    }

    @Test
    fun `stage4 create mapping`() {
        val mapping = ElasticSearchMapping(
            mapOf(
                "id" to MappingProperties.LONG,
                "name" to MappingProperty(MappingTypes.TEXT, "autocomplete"),
                "title" to MappingProperties.OBJECT,
                "techinicalName" to MappingProperties.TEXT,
                "attachAllowed" to MappingProperties.BOOLEAN,
                "parentId" to MappingProperties.LONG
            )
        ).withTypeName("all")
        val result = elasticSearchAdministration!!.createMapping(mapping).toCompletableFuture().get(timeOut, TimeUnit.SECONDS)
        assertThat(result).isEqualTo(Done.done())
    }

    @Test
    fun `stage5 deleteIndex programatically`() {
        val result = elasticSearchAdministration!!.deleteIndex().toCompletableFuture().get()
        assertThat(result).isEqualTo(Done.done())
    }
}
