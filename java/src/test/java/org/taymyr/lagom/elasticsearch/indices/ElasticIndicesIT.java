package org.taymyr.lagom.elasticsearch.indices;

import akka.Done;
import akka.japi.Pair;
import com.google.common.collect.ImmutableMap;
import com.lightbend.lagom.javadsl.api.transport.TransportException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.AutocompleteFilter;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Analysis;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CustomAnalyzer;
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType;
import org.taymyr.lagom.elasticsearch.indices.dsl.DeleteIndicesResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.DynamicType;
import org.taymyr.lagom.elasticsearch.indices.dsl.IndexInfo;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.api.transport.TransportErrorCode.NotFound;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.mapping;

import static java.util.Arrays.asList;

class ElasticIndicesIT extends AbstractElasticsearchIT {

    @Nested
    @DisplayName("Index should")
    class CreateIndexIT {

        Pair<String, CreateIndex> def = new Pair<>("def", new CreateIndex());
        Pair<String, CreateIndex> custom = new Pair<>("custom", new CreateIndex(new Settings(3, 3)));

        @Test
        @DisplayName("successfully created")
        void shouldBeSuccessfullyCreated() throws InterruptedException, ExecutionException, TimeoutException {
            CreateIndexResult result = eventually(elasticIndices.create(def.first()).invoke(def.second()));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(def.first());

            result = eventually(elasticIndices.create(custom.first()).invoke(custom.second()));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(custom.first());

            assertThat(eventually(elasticIndices.exists(def.first()).invoke())).isEqualTo(Done.getInstance());
            assertThat(eventually(elasticIndices.exists(custom.first()).invoke())).isEqualTo(Done.getInstance());
            assertThat(eventually(elasticIndices.exists(asList(def.first(), custom.first())).invoke())).isEqualTo(Done.getInstance());

            Map<String, IndexInfo> getResult = eventually(elasticIndices.get(asList(def.first(), custom.first())).invoke());
            assertThat(getResult).containsOnlyKeys(def.first(), custom.first());
            IndexInfo.Index index = getResult.get(def.first()).getSettings().getIndex();
            assertThat(index.getNumberOfReplicas()).isEqualTo(1);
            assertThat(index.getNumberOfShards()).isEqualTo(5);

            index = getResult.get(custom.first()).getSettings().getIndex();
            assertThat(index.getNumberOfReplicas()).isEqualTo(custom.second().getSettings().getNumberOfReplicas());
            assertThat(index.getNumberOfShards()).isEqualTo(custom.second().getSettings().getNumberOfShards());

            DeleteIndicesResult deleteResult = eventually(elasticIndices.delete(asList(def.first(), custom.first())).invoke());
            assertThat(deleteResult.getAcknowledged()).isTrue();
        }

        @Test
        @DisplayName("not exists if not created")
        void shouldBeNotExistsIfNotCreated() {
            Throwable thrown = catchThrowable(() -> eventually(elasticIndices.exists("test2").invoke()));
            assertThat(thrown).isInstanceOf(ExecutionException.class).hasCauseInstanceOf(TransportException.class);
            assertThat(((TransportException) thrown.getCause()).errorCode()).isEqualTo(NotFound);
        }


        @Test
        @DisplayName("successfully created with filter settings")
        void shouldSuccessfullyCreatedWithFilterSettings() throws InterruptedException, ExecutionException, TimeoutException {
            CreateIndex request = new CreateIndex(
                    new Settings(1, 1, new Analysis(
                            ImmutableMap.of(
                                    "autocomplete_filter", new AutocompleteFilter("edge_ngram", 1, 20)
                            ),
                            ImmutableMap.of(
                                    "autocomplete", new CustomAnalyzer("standard", asList("lowercase", "autocomplete_filter"))
                            )
                    )),
                    ImmutableMap.of("some_type", new Mapping(
                            ImmutableMap.of(
                                    "id", MappingProperty.LONG,
                                    "name", mapping().type(DataType.TEXT).analyzer("autocomplete").build(),
                                    "title", MappingProperty.OBJECT,
                                    "technicalName", MappingProperty.TEXT,
                                    "attachAllowed", MappingProperty.BOOLEAN
                            )
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create("test").invoke(request));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo("test");
        }

    }

    @Nested
    @DisplayName("Index with dynamic mappings should")
    class IndexWithDynamicMappingsIT {

        @Test
        @DisplayName("successfully created with default dynamic property")
        void shouldSuccessfullyCreated() throws InterruptedException, ExecutionException, TimeoutException {
            String indexName = "dynamic_default";
            CreateIndex createDynamicDefault = new CreateIndex(
                    new Settings(1, 1),
                    ImmutableMap.of("_doc", new Mapping(
                            ImmutableMap.of("id", MappingProperty.LONG)
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(createDynamicDefault));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(indexName);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(indexName).invoke());
            assertThat(indices).containsOnlyKeys(indexName);
            Map<String, Mapping> mappings = indices.get(indexName).getMappings();
            assertThat(mappings).containsOnlyKeys("_doc");
            assertThat(mappings.get("_doc").getDynamic()).isNull();
        }

        @Test
        @DisplayName("successfully created with `true` dynamic property")
        void shouldSuccessfullyCreatedWithDynamicTrue() throws InterruptedException, ExecutionException, TimeoutException {
            String indexName = "dynamic_true";
            CreateIndex createDynamicTrue = new CreateIndex(
                    new Settings(1, 1),
                    ImmutableMap.of("_doc", new Mapping(
                            ImmutableMap.of("id", MappingProperty.LONG),
                            DynamicType.TRUE
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(createDynamicTrue));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(indexName);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(indexName).invoke());
            assertThat(indices).containsOnlyKeys(indexName);
            Map<String, Mapping> mappings = indices.get(indexName).getMappings();
            assertThat(mappings).containsOnlyKeys("_doc");
            assertThat(mappings.get("_doc").getDynamic()).isEqualTo(DynamicType.TRUE);
        }

        @Test
        @DisplayName("successfully created with `false` dynamic property")
        void shouldSuccessfullyCreatedWithDynamicFalse() throws InterruptedException, ExecutionException, TimeoutException {
            String indexName = "dynamic_false";
            CreateIndex createDynamicFalse = new CreateIndex(
                    new Settings(1, 1),
                    ImmutableMap.of("_doc", new Mapping(
                            ImmutableMap.of("id", MappingProperty.LONG),
                            DynamicType.FALSE
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(createDynamicFalse));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(indexName);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(indexName).invoke());
            assertThat(indices).containsOnlyKeys(indexName);
            Map<String, Mapping> mappings = indices.get(indexName).getMappings();
            assertThat(mappings).containsOnlyKeys("_doc");
            assertThat(mappings.get("_doc").getDynamic()).isEqualTo(DynamicType.FALSE);
        }

        @Test
        @DisplayName("successfully created with `strict` dynamic property")
        void shouldSuccessfullyCreatedWithDynamicStrict() throws InterruptedException, ExecutionException, TimeoutException {
            String indexName = "dynamic_strict";
            CreateIndex createIndex = new CreateIndex(
                    new Settings(1, 1),
                    ImmutableMap.of("_doc", new Mapping(
                            ImmutableMap.of("id", MappingProperty.LONG),
                            DynamicType.STRICT
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(createIndex));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(indexName);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(indexName).invoke());
            assertThat(indices).containsOnlyKeys(indexName);
            Map<String, Mapping> mappings = indices.get(indexName).getMappings();
            assertThat(mappings).containsOnlyKeys("_doc");
            assertThat(mappings.get("_doc").getDynamic()).isEqualTo(DynamicType.STRICT);
        }

        @Test
        @DisplayName("successfully created with dynamic properties")
        void shouldSuccessfullyCreatedWithDynamicProperties() throws InterruptedException, ExecutionException, TimeoutException {
            String indexName = "dynamic_props";
            CreateIndex createIndex = new CreateIndex(
                    new Settings(1, 1),
                    ImmutableMap.of("_doc", new Mapping(
                            ImmutableMap.of(
                                    "name", MappingProperty.TEXT,
                                    "social_networks", mapping()
                                            .type(DataType.OBJECT)
                                            .dynamic(DynamicType.TRUE)
                                            .properties(ImmutableMap.of())
                                            .build()
                            ),
                            DynamicType.FALSE
                    ))
            );
            CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(createIndex));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(indexName);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(indexName).invoke());
            assertThat(indices).containsOnlyKeys(indexName);
            Map<String, Mapping> mappings = indices.get(indexName).getMappings();
            assertThat(mappings).containsOnlyKeys("_doc");
            assertThat(mappings.get("_doc").getDynamic()).isEqualTo(DynamicType.FALSE);
            assertThat(mappings.get("_doc").getProperties()).containsKeys("social_networks");
            assertThat(mappings.get("_doc").getProperties().get("social_networks").getDynamic()).isEqualTo(DynamicType.TRUE);
        }
    }

    @Nested
    @DisplayName("Index with aggregated fields should")
    class IndexWithAggregatedFields {

        String aggregatedFieldsIndex = "aggregated_fields";
        String aggregatedFieldsDoc = "aggregated_fields";
        String aggregateTextField = "aggregate_text";
        List<String> aggregateText = asList(aggregateTextField);

        @Test
        @DisplayName("successfully created")
        void shouldSuccessfullyCreated() throws InterruptedException, ExecutionException, TimeoutException {
            CreateIndex request = new CreateIndex(
                new Settings(1, 1),
                ImmutableMap.of(
                    aggregatedFieldsDoc, new Mapping(
                        ImmutableMap.of(
                            "user", MappingProperty.KEYWORD,
                            "message", MappingProperty.TEXT,
                            aggregateTextField, mapping().type(DataType.TEXT).analyzer("russian").build(),
                            "nested_obj", mapping().type(DataType.OBJECT).properties(
                                ImmutableMap.of(
                                    "text_field", mapping().type(DataType.TEXT).copyTo(aggregateText).build(),
                                    "keyword_field", mapping().type(DataType.KEYWORD).copyTo(aggregateText).build(),
                                    "integer_field", mapping().type(DataType.LONG).copyTo(aggregateText).build(),
                                    "date_field", mapping().type(DataType.DATE).copyTo(aggregateText).build()
                                )
                            ).build()
                        )
                    )
                )
            );

            CreateIndexResult result = eventually(elasticIndices.create(aggregatedFieldsIndex).invoke(request));
            assertThat(result.getAcknowledged()).isTrue();
            assertThat(result.getShardsAcknowledged()).isTrue();
            assertThat(result.getIndex()).isEqualTo(aggregatedFieldsIndex);

            Map<String, IndexInfo> indices = eventually(elasticIndices.get(aggregatedFieldsIndex).invoke());
            assertThat(indices).containsOnlyKeys(aggregatedFieldsIndex);
            assertThat(indices.get(aggregatedFieldsIndex).getMappings()).containsOnlyKeys(aggregatedFieldsDoc);
            Mapping mapping = indices.get(aggregatedFieldsIndex).getMappings().get(aggregatedFieldsDoc);
            assertThat(mapping.getProperties()).containsKeys(aggregateTextField, "nested_obj");
            MappingProperty property = mapping.getProperties().get(aggregateTextField);
            assertThat(property.getAnalyzer()).isEqualTo("russian");
            Map<String, MappingProperty> nestedProps = mapping.getProperties().get("nested_obj").getProperties();
            assertThat(nestedProps).containsKeys("text_field", "keyword_field", "integer_field", "date_field");

            assertThat(nestedProps.get("text_field").getCopyTo()).containsExactly(aggregateTextField);
            assertThat(nestedProps.get("keyword_field").getCopyTo()).containsExactly(aggregateTextField);
            assertThat(nestedProps.get("integer_field").getCopyTo()).containsExactly(aggregateTextField);
            assertThat(nestedProps.get("date_field").getCopyTo()).containsExactly(aggregateTextField);
        }
    }
}