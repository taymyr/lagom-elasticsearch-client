package org.taymyr.lagom.elasticsearch.test

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic
import pl.allegro.tech.embeddedelasticsearch.PopularProperties
import java.util.concurrent.TimeUnit

abstract class AbstractEmbeddedElastic {

    companion object {

        var embeddedElastic: EmbeddedElastic? = null

        init {
            embeddedElastic = EmbeddedElastic.builder()
                .withElasticVersion("6.4.1")
                .withSetting(PopularProperties.CLUSTER_NAME, "my_cluster")
                .withSetting(PopularProperties.HTTP_PORT, "9250")
                .withEsJavaOpts("-Xms128m -Xmx512m")
                .withStartTimeout(5, TimeUnit.MINUTES)
                .build()
        }
    }
}