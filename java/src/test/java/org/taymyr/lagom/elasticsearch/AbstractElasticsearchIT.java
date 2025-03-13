package org.taymyr.lagom.elasticsearch;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.lightbend.lagom.javadsl.client.integration.LagomClientFactory;
import com.typesafe.config.Config;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.taymyr.lagom.elasticsearch.document.ElasticDocument;
import org.taymyr.lagom.elasticsearch.indices.ElasticIndices;
import org.taymyr.lagom.elasticsearch.search.ElasticSearch;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import play.api.Configuration;
import scala.collection.immutable.Map$;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Testcontainers
public class AbstractElasticsearchIT {

    static final String ELASTIC_VERSION = "7.17.28";

    protected static Config config;
    protected static ActorSystem actorSystem;
    protected static Materializer materializer;
    protected static LagomClientFactory clientFactory;
    protected static ElasticSearch elasticSearch;
    protected static ElasticIndices elasticIndices;
    protected static ElasticDocument elasticDocument;

    @Container
    protected static final ElasticsearchContainer elastic = new ElasticsearchContainer(
        DockerImageName.parse("elasticsearch:"  + ELASTIC_VERSION)
            .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch:" + ELASTIC_VERSION)
    );

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        ClassLoader classLoader = LagomClientFactory.class.getClassLoader();
        config = Configuration.load(classLoader, System.getProperties(), Map$.MODULE$.empty(), true)
            .underlying();
        actorSystem = ActorSystem.create("elasticsearch-it", config, classLoader);
        materializer = ActorMaterializer.create(actorSystem);
        clientFactory = LagomClientFactory.create("elastic-search", classLoader, actorSystem, materializer);
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

    protected static <T> T loggingTimings(String message, ThrowingSupplier<T> block) throws Throwable {
        Instant startTime = Instant.now();
        T result = block.get();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println(message + ". Took: " + duration.toMillis() + " ms");
        return result;
    }
}
