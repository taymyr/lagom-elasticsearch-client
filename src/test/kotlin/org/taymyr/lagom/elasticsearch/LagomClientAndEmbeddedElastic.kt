package org.taymyr.lagom.elasticsearch

import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import org.taymyr.lagom.elasticsearch.document.ElasticDocument
import org.taymyr.lagom.elasticsearch.indices.ElasticIndices
import org.taymyr.lagom.elasticsearch.search.ElasticSearch
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import pl.allegro.tech.embeddedelasticsearch.PopularProperties.CLUSTER_NAME
import pl.allegro.tech.embeddedelasticsearch.PopularProperties.HTTP_PORT
import java.net.URI
import java.util.concurrent.TimeUnit

private const val ELASTIC_PORT = "9250"
open class LagomClientAndEmbeddedElastic : TestListener {

    lateinit var clientFactory: LagomClientFactory
    lateinit var embeddedElastic: EmbeddedElastic
    lateinit var elasticSearch: ElasticSearch
    lateinit var elasticIndices: ElasticIndices
    lateinit var elasticDocument: ElasticDocument

    override fun beforeSpec(description: Description, spec: Spec) {
        clientFactory = LagomClientFactory.create(
            "elastic-search",
            LagomClientFactory::class.java.classLoader
        )
        elasticSearch = clientFactory.createClient(ElasticSearch::class.java, URI("http://localhost:$ELASTIC_PORT"))
        elasticIndices = clientFactory.createClient(ElasticIndices::class.java, URI("http://localhost:$ELASTIC_PORT"))
        elasticDocument = clientFactory.createClient(ElasticDocument::class.java, URI("http://localhost:$ELASTIC_PORT"))
        embeddedElastic = EmbeddedElastic.builder()
            .withElasticVersion("6.4.1")
            .withSetting(CLUSTER_NAME, "test_cluster")
            .withSetting(HTTP_PORT, ELASTIC_PORT)
            .withEsJavaOpts("-Xms128m -Xmx512m")
            .withStartTimeout(5, TimeUnit.MINUTES)
            .build()
            .start()
    }

    override fun afterSpec(description: Description, spec: Spec) {
        clientFactory.close()
        embeddedElastic.stop()
    }
}
