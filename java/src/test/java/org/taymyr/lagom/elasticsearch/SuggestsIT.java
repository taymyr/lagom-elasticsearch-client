package org.taymyr.lagom.elasticsearch;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.taymyr.lagom.elasticsearch.search.dsl.query.Fuzzy;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.CompletionSuggest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.mapping;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;

class SuggestsIT extends AbstractElasticsearchIT {

    @Test
    @DisplayName("Completion suggest should be correct")
    void shouldBeCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createIndex();
        addItems();
        search();
    }

    // successfully create new index
    void createIndex() throws InterruptedException, ExecutionException, TimeoutException {
        Map<String, MappingProperty> mappings = new HashMap<>();
        mappings.put("id", MappingProperty.LONG);
        mappings.put("category.id", MappingProperty.LONG);
        mappings.put("category.title", MappingProperty.KEYWORD);
        mappings.put("fullTextBoosted", mapping().type(DataType.TEXT).analyzer("russian").build());
        mappings.put("fullText", mapping().type(DataType.TEXT).analyzer("russian").build());
        mappings.put("sellerId", MappingProperty.LONG);
        mappings.put("categoryIds", MappingProperty.LONG);
        mappings.put("updateDate", MappingProperty.DATE);
        mappings.put("basePrice", MappingProperty.INTEGER);
        mappings.put("suggest", MappingProperty.COMPLETION);
        mappings.put("payload", MappingProperty.OBJECT);
        CreateIndex request = new CreateIndex(
                new Settings(1, 1),
                ImmutableMap.of("product", new Mapping(mappings))
        );
        CreateIndexResult result = eventually(elasticIndices.create("product").invoke(request));
        assertThat(result.getIndex()).isEqualTo("product");
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
    }

    // successfully add some items
    void addItems() throws InterruptedException, ExecutionException, TimeoutException {
        IndexedSampleProduct testEntity = new IndexedSampleProduct(new SampleProduct(
                13,
                new SampleProduct.Category(100, "Ягоды"),
                "Клубника вкусная",
                "Россия Ягоды Фрукты",
                0,
                asList(100L, 200L, 600L),
                new Date(),
                300500,
                asList(
                        new SampleProduct.StaticFacets("country", "Аргентина"),
                        new SampleProduct.StaticFacets("salemethod", "Упаковка"),
                        new SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        new SampleProduct.StaticFacets("saleregion", "Мурманская область")
                ),
                asList("Клубника вкусная"),
                new SampleProduct.Payload("")
        ));
        IndexedSampleProduct testEntity2 = new IndexedSampleProduct(new SampleProduct(
                11,
                new SampleProduct.Category(200, "Овощи"),
                "Помидоры красные",
                "Россия Овощи",
                0,
                asList(100L, 200L, 500L),
                new Date(),
                200500,
                asList(
                        new SampleProduct.StaticFacets("country", "Украина"),
                        new SampleProduct.StaticFacets("salemethod", "Поштучно"),
                        new SampleProduct.StaticFacets("saleregion", "Ленинградская область"),
                        new SampleProduct.StaticFacets("saleregion", "Московская область")
                ),
                asList("Помидоры красные"),
                new SampleProduct.Payload("")
        ));
        BulkRequest request = BulkRequest.ofCommands(
                new BulkCreate("13", testEntity),
                new BulkCreate("11", testEntity2)
        );
        BulkResult result = eventually(elasticDocument.bulk("product", "product").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(2);
    }


    // do search
    void search() throws InterruptedException, ExecutionException, TimeoutException {
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
                null,
                null,
                ImmutableMap.of(
                        "mySuggest", new CompletionSuggest("помадор", new CompletionSuggest.Completion(
                                "suggest",
                                Fuzzy.auto()
                        ))
                )
        );
        SampleProductResult result = eventually(invokeT(elasticSearch.search("product", "product"), searchRequest, SampleProductResult.class));
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getSuggest()).containsKeys("mySuggest");
        assertThat(result.getSuggest().get("mySuggest").get(0).getOptions().get(0).getText()).isEqualTo("Помидоры красные");
    }
}