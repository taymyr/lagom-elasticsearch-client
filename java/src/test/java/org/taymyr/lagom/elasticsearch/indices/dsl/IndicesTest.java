package org.taymyr.lagom.elasticsearch.indices.dsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Analysis;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;

import java.io.IOException;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

import static java.util.Arrays.asList;

class IndicesTest {

    @Test
    @DisplayName("successfully serialize create index with filter settings")
    void shouldSuccessfullySerializeCreateIndexWithFilterSettings() {
        CreateIndex request = new CreateIndex(
            new Settings(1, 2, new Analysis(
                ImmutableMap.of(
                    "autocomplete_filter", new AutocompleteFilter("edge_ngram", 1, 20)
                ),
                ImmutableMap.of(
                    "autocomplete", new CustomAnalyzer("standard", asList("lowercase", "autocomplete_filter"))
                )
            )),
            new Mapping(
                ImmutableMap.<String, MappingProperty>builder()
                    .put("id", MappingProperty.builder().type(DataType.LONG).dynamic(DynamicType.TRUE).build())
                    .put("name", MappingProperty.builder().type(DataType.TEXT).analyzer("autocomplete").build())
                    .put("title", MappingProperty.OBJECT)
                    .put("technicalName", MappingProperty.TEXT)
                    .put("attachAllowed", MappingProperty.BOOLEAN)
                    .put("suggest", MappingProperty.builder()
                        .type(DataType.COMPLETION)
                        .context(Context.builder()
                            .name("name")
                            .type(ContextType.CATEGORY)
                            .path("name")
                            .build())
                        .build())
                    .put("fields", MappingProperty.builder().type(DataType.OBJECT).fields(
                        ImmutableMap.of("enabled", MappingProperty.BOOLEAN)
                    ).build())
                    .build()
            )
        );
        String actual = serializeRequest(request, CreateIndex.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_filter.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize create index with aggregation")
    void shouldSuccessfullyCreated() {
        String aggregatedFieldsDoc = "aggregated_fields";
        String aggregateTextField = "aggregate_text";
        List<String> aggregateText = asList(aggregateTextField);
        CreateIndex request = new CreateIndex(
            new Settings(1, 1),
            new Mapping(
                ImmutableMap.of(
                    "user", MappingProperty.KEYWORD,
                    "message", MappingProperty.TEXT,
                    "suggest", MappingProperty.COMPLETION,
                    aggregateTextField, MappingProperty.builder().type(DataType.TEXT).analyzer("russian").build(),
                    "nested_obj", MappingProperty.builder().type(DataType.OBJECT).properties(
                        ImmutableMap.of(
                            "text_field", MappingProperty.builder().type(DataType.TEXT).copyTo(aggregateTextField).build(),
                            "keyword_field", MappingProperty.builder().type(DataType.KEYWORD).copyTo(aggregateText).build(),
                            "integer_field", MappingProperty.builder().type(DataType.LONG).copyTo(aggregateText).build(),
                            "date_field", MappingProperty.builder().type(DataType.DATE).format("yyyy-MM-dd").copyTo(aggregateText).build()
                        )
                    ).build()
                )
            )
        );
        String actual = serializeRequest(request, CreateIndex.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_aggregation.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize create index with index settings")
    void shouldSuccessfullyCreatedWithIndexSettings() {
        CreateIndex request = new CreateIndex(
                new Settings(
                        null,
                        null,
                        new Analysis(
                                null,
                                ImmutableMap.of(
                                        "lowercase_analyzer", new CustomAnalyzer("standard", asList("lowercase")),
                                        "stop_analyzer", new CustomAnalyzer("standard", asList("lowercase", "stop"))
                                )
                        ),
                        ImmutableMap.of(
                                "refresh_interval", "1s",
                                "max_result_window", 20000
                        )
                ),
                new Mapping(
                        ImmutableMap.<String, MappingProperty>builder()
                                .put("id", MappingProperty.KEYWORD)
                                .put("name", MappingProperty.builder()
                                        .type(DataType.TEXT)
                                        .analyzer("lowercase_analyzer")
                                        .searchAnalyzer("stop_analyzer")
                                        .fields(ImmutableMap.of("keyword", MappingProperty.KEYWORD))
                                        .build())
                                .put("price", MappingProperty.FLOAT)
                                .put("quantity", MappingProperty.SHORT)
                                .put("priority", MappingProperty.BYTE)
                                .put("url", MappingProperty.builder()
                                        .type(DataType.KEYWORD)
                                        .index(false)
                                        .build())
                                .build(),
                        DynamicType.STRICT
                )
        );
        String actual = serializeRequest(request, CreateIndex.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_index_settings.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect mapping")
    void shouldThrowExceptionForIncorrectMapping() {
        assertThatThrownBy(() -> MappingProperty.builder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Field 'type' can not be null");
    }

    @Test
    @DisplayName("successfully deserialize create index result")
    void shouldSuccessfullyDeserializeCreateIndexResult() {
        CreateIndexResult result = deserializeResponse(
            resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_result.json"),
            CreateIndexResult.class
        );
        assertThat(result.getIndex()).isEqualTo("test");
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
    }

    @Test
    @DisplayName("successfully deserialize delete index result")
    void shouldSuccessfullyDeserializeDeleteIndexResult() {
        DeleteIndicesResult result = deserializeResponse(
            resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/delete_result.json"),
            DeleteIndicesResult.class
        );
        assertThat(result.getAcknowledged()).isTrue();
    }

    @Test
    @DisplayName("successfully deserialize delete index result")
    void shouldSuccessfullyDeserializeGetIndexInfo() {
        IndexInfo result = deserializeResponse(
            resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/get_result.json"),
            IndexInfo.class
        );
        IndexInfo.Settings settings = new IndexInfo.Settings(new IndexInfo.Index(1, 2));
        assertThat(result.getSettings()).isEqualTo(settings);
        assertThat(result.getMappings()).hasSize(1);
        Mapping mapping = result.getMappings().get("type");
        assertThat(mapping.getDynamic()).isNull();
        assertThat(mapping.getProperties()).hasSize(1);
        MappingProperty mappingProperty = mapping.getProperties().get("id");
        assertThat(mappingProperty.getType()).isEqualTo(DataType.LONG);
    }

    @Test
    @DisplayName("successfully deserialize create index with index settings")
    void shouldSuccessfullyDeserializeCreateIndexWithSettings() {
        CreateIndex result = deserializeResponse(
                resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_index_settings.json"),
                CreateIndex.class
        );
        Settings settings = result.getSettings();
        assertThat(settings).isNotNull();
        assertThat(settings.getNumberOfShards()).isNull();
        assertThat(settings.getNumberOfReplicas()).isNull();
        assertThat(settings.getIndex()).containsOnly(entry("refresh_interval", "1s"), entry("max_result_window", 20000));
        Analysis analysis = settings.getAnalysis();
        assertThat(analysis).isNotNull();
        assertThat(analysis.getFilter()).isNull();
        assertThat(analysis.getAnalyzer()).hasSize(2);
        assertThat(analysis.getAnalyzer().get("stop_analyzer"))
                .isEqualTo(new CustomAnalyzer("standard", asList("lowercase", "stop")));
        Mapping mapping = result.getMappings();
        assertThat(mapping).isNotNull();
        assertThat(mapping.getDynamic()).isEqualTo(DynamicType.STRICT);
        assertThat(mapping.getProperties()).hasSize(6);
        assertThat(mapping.getProperties().get("name").getSearchAnalyzer()).isEqualTo("stop_analyzer");
        assertThat(mapping.getProperties().get("price").getType()).isEqualTo(DataType.FLOAT);
        assertThat(mapping.getProperties().get("quantity").getType()).isEqualTo(DataType.SHORT);
        assertThat(mapping.getProperties().get("priority").getType()).isEqualTo(DataType.BYTE);
        assertThat(mapping.getProperties().get("url").getIndex()).isFalse();
    }

    @Test
    @DisplayName("successfully deserialize create index with registered filter")
    void shouldSuccessfullyDeserializeCreateIndexWithRegisteredFilter() throws IOException {
        ObjectMapper mapper = ElasticSerializerFactory.getMAPPER().copy();
        mapper.registerSubtypes(new NamedType(AutocompleteFilter.class, "edge_ngram"));
        CreateIndex result = mapper.readValue(
                resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_filter.json"),
                CreateIndex.class
        );
        Settings settings = result.getSettings();
        assertThat(settings).isNotNull();
        Analysis analysis = settings.getAnalysis();
        assertThat(analysis).isNotNull();
        assertThat(analysis.getFilter()).hasSize(1);
        Filter filter = analysis.getFilter().get("autocomplete_filter");
        assertThat(filter).isInstanceOf(AutocompleteFilter.class);
        assertThat(filter.getType()).isEqualTo("edge_ngram");
        assertThat(((AutocompleteFilter) filter).getMinGram()).isEqualTo(1);
        assertThat(((AutocompleteFilter) filter).getMaxGram()).isEqualTo(20);
        assertThat(analysis.getAnalyzer()).hasSize(1);
        assertThat(analysis.getAnalyzer().get("autocomplete"))
                .isEqualTo(new CustomAnalyzer("standard", asList("lowercase", "autocomplete_filter")));
    }

    @Test
    @DisplayName("throw exception for unregistered filter")
    void shouldThrowExceptionForUnregisteredFilter() {
        assertThatThrownBy(() -> ElasticSerializerFactory.getMAPPER().readValue(
                resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_filter.json"),
                CreateIndex.class
        ))
                .isInstanceOf(InvalidTypeIdException.class)
                .hasMessageContaining("edge_ngram");
    }
}
