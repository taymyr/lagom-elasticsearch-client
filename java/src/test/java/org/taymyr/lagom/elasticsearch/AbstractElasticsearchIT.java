package org.taymyr.lagom.elasticsearch;

import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.taymyr.lagom.elasticsearch.document.ElasticDocument;
import org.taymyr.lagom.elasticsearch.indices.ElasticIndices;
import org.taymyr.lagom.elasticsearch.search.ElasticSearch;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static pl.allegro.tech.embeddedelasticsearch.PopularProperties.CLUSTER_NAME;
import static pl.allegro.tech.embeddedelasticsearch.PopularProperties.HTTP_PORT;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AbstractElasticsearchIT {

    static final String ELASTIC_PORT = "9250";

    protected static LagomClientFactory clientFactory;
    protected static EmbeddedElastic embeddedElastic;
    protected static ElasticSearch elasticSearch;
    protected static ElasticIndices elasticIndices;
    protected static ElasticDocument elasticDocument;

    @BeforeAll
    static void beforeAll() throws URISyntaxException, IOException, InterruptedException {
        clientFactory = LagomClientFactory.create("elastic-search",
                LagomClientFactory.class.getClassLoader()
        );
        elasticSearch = clientFactory.createClient(ElasticSearch.class, new URI("http://localhost:" + ELASTIC_PORT));
        elasticIndices = clientFactory.createClient(ElasticIndices.class, new URI("http://localhost:" + ELASTIC_PORT));
        elasticDocument = clientFactory.createClient(ElasticDocument.class, new URI("http://localhost:" + ELASTIC_PORT));
        embeddedElastic = EmbeddedElastic.builder()
                .withElasticVersion("6.4.1")
                .withSetting(CLUSTER_NAME, "test_cluster")
                .withSetting(HTTP_PORT, ELASTIC_PORT)
                .withEsJavaOpts("-Xms128m -Xmx512m")
                .withStartTimeout(5, TimeUnit.MINUTES)
                .build()
                .start();
    }

    @AfterAll
    static void afterAll() {
        clientFactory.close();
        embeddedElastic.stop();
    }

    protected static <T> T eventually(CompletionStage<T> stage) throws InterruptedException, ExecutionException, TimeoutException {
        return stage.toCompletableFuture().get(5, SECONDS);
    }
}
