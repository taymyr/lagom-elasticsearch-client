package org.taymyr.lagom.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.SampleProduct.Category;
import org.taymyr.lagom.elasticsearch.SampleProduct.Payload;
import org.taymyr.lagom.elasticsearch.SampleProduct.StaticFacets;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.CompositeAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.FilterAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.NestedAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation;
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.TermsAggregation.FieldSpec;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.joining.NestedQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.joining.NestedQueryBody;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Term;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermsQuery;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.mapping;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;

class AggregationsIT extends AbstractElasticsearchIT {

    @Test
    @DisplayName("Aggregations should be correct")
    void shouldBeCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createIndex();
        addItems();
        search();
    }

    void createIndex() throws InterruptedException, ExecutionException, TimeoutException {
        CreateIndex request = new CreateIndex(
                new Settings(1, 1),
                ImmutableMap.of(
                        "product", new Mapping(ImmutableMap.<String, MappingProperty>builder()
                                .put("id", MappingProperty.LONG)
                                .put("category.id", MappingProperty.LONG)
                                .put("category.title", MappingProperty.KEYWORD)
                                .put("fullTextBoosted", mapping().type(DataType.TEXT).analyzer("russian").build())
                                .put("fullText", mapping().type(DataType.TEXT).analyzer("russian").build())
                                .put("sellerId", MappingProperty.LONG)
                                .put("categoryIds", MappingProperty.LONG)
                                .put("updateDate", MappingProperty.DATE)
                                .put("basePrice", MappingProperty.INTEGER)
                                .put("staticFacets", mapping().type(DataType.NESTED).properties(ImmutableMap.of(
                                        "name", MappingProperty.KEYWORD,
                                        "value", MappingProperty.KEYWORD
                                )).build())
                                .put("payload", MappingProperty.OBJECT)
                                .build()
                        ))
        );
        CreateIndexResult result = eventually(elasticIndices.create("product").invoke(request));
        assertThat(result.getIndex()).isEqualTo("product");
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
    }

    void addItems() throws InterruptedException, ExecutionException, TimeoutException {
        IndexedSampleProduct testEntity = new IndexedSampleProduct(new SampleProduct(
                13,
                new Category(100, "Ягоды"),
                "Клубника вкусная",
                "Россия Ягоды Фрукты",
                0,
                asList(100L, 200L, 600L),
                new Date(),
                300500,
                asList(
                        new StaticFacets("country", "Аргентина"),
                        new StaticFacets("salemethod", "Упаковка"),
                        new StaticFacets("saleregion", "Ленинградская область"),
                        new StaticFacets("saleregion", "Мурманская область")
                ),
                null,
                new Payload("")
        ));
        IndexedSampleProduct testEntity2 = new IndexedSampleProduct(new SampleProduct(
                11,
                new Category(200, "Овощи"),
                "Помидоры красные",
                "Россия Овощи",
                0,
                asList(100L, 200L, 500L),
                new Date(),
                200500,
                asList(
                        new StaticFacets("country", "Украина"),
                        new StaticFacets("salemethod", "Поштучно"),
                        new StaticFacets("saleregion", "Ленинградская область"),
                        new StaticFacets("saleregion", "Московская область")
                ),
                null,
                new Payload("")
        ));
        BulkRequest request = BulkRequest.ofCommands(
                new BulkCreate("13", testEntity),
                new BulkCreate("11", testEntity2)
        );
        BulkResult result = eventually(elasticDocument.bulk("product", "product").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(2);
    }

    void search() throws InterruptedException, TimeoutException, ExecutionException {
        sleep(1000);
        SearchRequest searchRequest = new SearchRequest(
                MatchQuery.ofMatch(new Match() {
                    private Long id = 11L;

                    public Long getId() {
                        return id;
                    }
                }),
                null,
                null,
                ImmutableMap.of(
                        "staticStringFacetsFiltered", FilterAggregation.of(
                                new NestedQuery(
                                        new NestedQueryBody(
                                                "staticFacets",
                                                BoolQuery.boolQuery().filter(
                                                        TermQuery.ofTerm(new Term() {
                                                            @JsonProperty("staticFacets.name")
                                                            private String staticFacetsName = "country";

                                                            public String getStaticFacetsName() {
                                                                return staticFacetsName;
                                                            }
                                                        }),
                                                        TermsQuery.ofTerms(ImmutableMap.of(
                                                                "staticFacets.value", asList("Украина")
                                                        ))
                                                ).build()
                                        )
                                ),
                                ImmutableMap.of(
                                        "staticStringFacets", NestedAggregation.of(
                                                "staticFacets",
                                                ImmutableMap.of(
                                                        "name", TermsAggregation.of(
                                                                new FieldSpec("staticFacets.name"),
                                                                ImmutableMap.of(
                                                                        "value", TermsAggregation.of(new FieldSpec("staticFacets.value"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        ),
                        "staticStringFacets", NestedAggregation.of(
                                "staticFacets",
                                ImmutableMap.of(
                                        "name", TermsAggregation.of(
                                                new FieldSpec("staticFacets.name"),
                                                ImmutableMap.of(
                                                        "value", TermsAggregation.of(
                                                                new FieldSpec("staticFacets.value")
                                                        )
                                                )
                                        )
                                )
                        ),
                        "categories", new CompositeAggregation(
                                new CompositeAggregation.Composite(asList(
                                        ImmutableMap.of(
                                                "categoryId", new TermsAggregation(new FieldSpec("category.id"))
                                        ),
                                        ImmutableMap.of(
                                                "categoryTitle", new TermsAggregation(new FieldSpec("category.title"))
                                        )
                                ))
                        )
                )
        );
        SampleProductResult result = eventually(invokeT(elasticSearch.search("product", "product"), searchRequest, SampleProductResult.class));
        assertThat(result.getTamedOut()).isFalse();
        SampleCategoryForProduct category = result.getTyped("/aggregations/categories/after_key", SampleCategoryForProduct.class);
        assertThat(category.getCategoryId()).isEqualTo(200);
    }
}