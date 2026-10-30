package org.taymyr.lagom.elasticsearch.indices;

import akka.Done;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.DeleteIndicesResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.IndexInfo;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;

class ElasticIndicesIT extends AbstractElasticsearchIT {

    @Test
    @DisplayName("Indices service descriptor should work correct")
    void shouldWorkCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createIndices();
        checkExists();
        getIndices();
        deleteIndices();
    }

    @Test
    @DisplayName("Indices service descriptor should create index with index settings")
    void shouldCreateIndexWithIndexSettings() throws InterruptedException, ExecutionException, TimeoutException {
        createIndexWithSettings();
        deleteIndexWithSettings();
    }

    private void createIndices() throws InterruptedException, ExecutionException, TimeoutException {
        CreateIndex request = new CreateIndex(new CreateIndex.Settings(1, 1));
        CreateIndexResult result = eventually(elasticIndices.create("index-1").invoke(request));
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
        assertThat(result.getIndex()).isEqualTo("index-1");
        result = eventually(elasticIndices.create("index-2").invoke(request));
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
        assertThat(result.getIndex()).isEqualTo("index-2");
    }

    private void checkExists() throws InterruptedException, ExecutionException, TimeoutException {
        Done result = eventually(elasticIndices.exists("index-1").invoke());
        assertThat(result).isEqualTo(Done.getInstance());
        result = eventually(elasticIndices.exists(asList("index-1", "index-2")).invoke());
        assertThat(result).isEqualTo(Done.getInstance());
    }

    private void getIndices() throws InterruptedException, ExecutionException, TimeoutException {
        Map<String, IndexInfo> result = eventually(elasticIndices.get("index-1").invoke());
        assertThat(result).hasSize(1);
        assertThat(result).containsKeys("index-1");
        result = eventually(elasticIndices.get(asList("index-1", "index-2")).invoke());
        assertThat(result).hasSize(2);
        assertThat(result).containsKeys("index-1", "index-2");
    }

    private void deleteIndices() throws InterruptedException, ExecutionException, TimeoutException {
        DeleteIndicesResult result = eventually(elasticIndices.delete("index-1").invoke());
        assertThat(result.getAcknowledged()).isTrue();
        result = eventually(elasticIndices.delete(singletonList("index-2")).invoke());
        assertThat(result.getAcknowledged()).isTrue();
    }

    private void createIndexWithSettings() throws InterruptedException, ExecutionException, TimeoutException {
        CreateIndex request = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_index_settings.json"),
                CreateIndex.class
        );
        CreateIndexResult result = eventually(elasticIndices.create("index-3").invoke(request));
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
        assertThat(result.getIndex()).isEqualTo("index-3");
    }

    private void deleteIndexWithSettings() throws InterruptedException, ExecutionException, TimeoutException {
        DeleteIndicesResult result = eventually(elasticIndices.delete("index-3").invoke());
        assertThat(result.getAcknowledged()).isTrue();
    }
}
