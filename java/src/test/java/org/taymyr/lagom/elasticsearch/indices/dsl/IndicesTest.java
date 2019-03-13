package org.taymyr.lagom.elasticsearch.indices.dsl;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Analysis;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;

import java.util.List;

import static java.util.Arrays.asList;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.deserializeResponse;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

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
            ImmutableMap.of("some_type", new Mapping(
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
            ImmutableMap.of(
                aggregatedFieldsDoc, new Mapping(
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
            )
        );
        String actual = serializeRequest(request, CreateIndex.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/indices/dsl/create_aggregation.json");
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

}