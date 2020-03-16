package org.taymyr.lagom.elasticsearch;

import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.taymyr.lagom.elasticsearch.document.ElasticDocument;
import org.taymyr.lagom.elasticsearch.indices.ElasticIndices;
import org.taymyr.lagom.elasticsearch.search.ElasticSearch;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Testcontainers
public class AbstractElasticsearchIT {

    static final String ELASTIC_VERSION = "7.4.1";

    protected static LagomClientFactory clientFactory;
    protected static ElasticSearch elasticSearch;
    protected static ElasticIndices elasticIndices;
    protected static ElasticDocument elasticDocument;

    @Container
    protected static final ElasticsearchContainer elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:" + ELASTIC_VERSION);

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        clientFactory = LagomClientFactory.create("elastic-search",
                LagomClientFactory.class.getClassLoader()
        );
        URI elasticAddress = new URI("http://" + elastic.getHttpHostAddress());
        elasticSearch = clientFactory.createClient(ElasticSearch.class, elasticAddress);
        elasticIndices = clientFactory.createClient(ElasticIndices.class, elasticAddress);
        elasticDocument = clientFactory.createClient(ElasticDocument.class, elasticAddress);
    }

    @AfterAll
    static void afterAll() {
        clientFactory.close();
    }

    protected static <T> T eventually(CompletionStage<T> stage) throws InterruptedException, ExecutionException, TimeoutException {
        return stage.toCompletableFuture().get(5, SECONDS);
    }

    protected static <T> T eventually(CompletionStage<T> stage, Duration duration) throws InterruptedException, ExecutionException, TimeoutException {
        return stage.toCompletableFuture().get(duration.get(ChronoUnit.SECONDS), SECONDS);
    }
}
