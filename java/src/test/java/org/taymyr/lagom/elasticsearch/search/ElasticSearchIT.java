package org.taymyr.lagom.elasticsearch.search;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.AutocompleteFilter;
import org.taymyr.lagom.elasticsearch.IndexedSampleCategory;
import org.taymyr.lagom.elasticsearch.MessageKeywordTerm;
import org.taymyr.lagom.elasticsearch.SampleCategory;
import org.taymyr.lagom.elasticsearch.SampleCategoryResult;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocumentResult;
import org.taymyr.lagom.elasticsearch.UserKeywordTerm;
import org.taymyr.lagom.elasticsearch.document.dsl.IndexDocumentResult;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Analysis;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CustomAnalyzer;
import org.taymyr.lagom.elasticsearch.indices.dsl.DataType;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty;
import org.taymyr.lagom.elasticsearch.search.dsl.HitResult;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.Match;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MultiMatchQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Ids;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invoke;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.mapping;

import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;

class ElasticSearchIT extends AbstractElasticsearchIT {

    static void generateCategories() throws InterruptedException, ExecutionException, TimeoutException {
        BulkRequest request = BulkRequest.ofCommands(
                new BulkCreate("1",
                        new IndexedSampleCategory(
                                new SampleCategory(
                                        1L,
                                        asList("test1"),
                                        null,
                                        null,
                                        true,
                                        "Овощи",
                                        "Огурцы"
                                )
                        )
                ),
                new BulkCreate("2",
                        new IndexedSampleCategory(
                                new SampleCategory(
                                        1L,
                                        asList("test1"),
                                        null,
                                        null,
                                        true,
                                        "Овощи",
                                        "Капуста"
                                )
                        )
                )
        );
        BulkResult result = eventually(elasticDocument.bulk("category", "some_type").invoke(request));
        assertThat(result.getErrors()).isFalse();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getStatus()).isEqualTo(201);
        assertThat(result.getItems().get(0).getResult()).isEqualTo("created");
        assertThat(result.getItems().get(0).getError()).isNull();
        assertThat(result.getItems().get(0).getIndex()).isEqualTo("category");
        assertThat(result.getItems().get(0).getType()).isEqualTo("some_type");
        assertThat(result.getItems().get(0).getId()).isEqualTo("1");
    }

    @BeforeAll
    static void createIndexAndData() throws InterruptedException, ExecutionException, TimeoutException {
        for (int it = 0; it < 10; ++it) {
            IndexDocumentResult result = eventually(invoke(elasticDocument.indexWithId("test", "sample", String.valueOf(it)),
                    new SampleDocument("user-" + it, "message-" + it)));
            assertThat(result.getIndex()).isEqualTo("test");
            assertThat(result.getType()).isEqualTo("sample");
        }
        CreateIndex request = new CreateIndex(
                new Settings(1, 1, new Analysis(
                        ImmutableMap.of(
                                "autocomplete_filter", new AutocompleteFilter(
                                        "edge_ngram",
                                        1,
                                        20
                                )
                        ),
                        ImmutableMap.of(
                                "autocomplete", new CustomAnalyzer(
                                        "standard",
                                        asList(
                                                "lowercase",
                                                "autocomplete_filter"
                                        )
                                )
                        )
                )),
                ImmutableMap.of(
                        "some_type", new Mapping(ImmutableMap.<String, MappingProperty>builder()
                                .put("id", MappingProperty.LONG)
                                .put("name", mapping().type(DataType.TEXT).analyzer("autocomplete").build())
                                .put("title", MappingProperty.OBJECT)
                                .put("technicalName", MappingProperty.TEXT)
                                .put("fullText", MappingProperty.TEXT)
                                .put("fullTextBoosted", MappingProperty.TEXT)
                                .put("attachAllowed", MappingProperty.BOOLEAN)
                                .build()
                        ))
        );
        CreateIndexResult result = eventually(elasticIndices.create("category").invoke(request));
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
        assertThat(result.getIndex()).isEqualTo("category");
        generateCategories();
        sleep(1000);
    }

    void check(SampleDocumentResult result) {
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getHits().getTotal()).isEqualTo(1);
        assertThat(result.getHits().getHits()).hasSize(1);
        assertThat(result.getHits().getHits().get(0).getScore()).isEqualTo(1.0);
        assertThat(result.getHits().getHits().get(0).getSource().getUser()).isEqualTo("user-2");
        assertThat(result.getHits().getHits().get(0).getSource().getMessage()).isEqualTo("message-2");
    }

    @Test
    @DisplayName("search a document by id")
    void searchById() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest(new IdsQuery(new Ids(asList("2"))));
        check(eventually(invokeT(elasticSearch.search(asList("test"), asList("sample")), searchRequest, SampleDocumentResult.class)));
        check(eventually(invokeT(elasticSearch.search(asList("test")), searchRequest, SampleDocumentResult.class)));
        check(eventually(invokeT(elasticSearch.search("test", "sample"), searchRequest, SampleDocumentResult.class)));
        check(eventually(invokeT(elasticSearch.search("test"), searchRequest, SampleDocumentResult.class)));
    }

    @Test
    @DisplayName("successful search document using autocomplete filter")
    void searchAutocompleteFilter() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest searchRequest = new SearchRequest(
                new MatchQuery(new Match() {
                    private String name = "test1";

                    public String getName() {
                        return name;
                    }
                })
        );
        SampleCategoryResult result = eventually(invokeT(elasticSearch.search("category", "some_type"), searchRequest, SampleCategoryResult.class));
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getHits().getTotal()).isEqualTo(2);
        assertThat(result.getHits().getHits()).hasSize(2);
        assertThat(result.getHits().getHits().get(0).getSource().getName()).containsExactly("test1");
    }

    @Test
    @DisplayName("successful search document using multi-match")
    void searchMultiMatch() throws InterruptedException, ExecutionException, TimeoutException {
        sleep(1_000);
        SearchRequest searchRequest = new SearchRequest(
                MultiMatchQuery.of(
                        "Огурцовые овощи",
                        ImmutableMap.of(
                                "fullTextBoosted", 10,
                                "fullText", 3
                        )
                )
        );
        SampleCategoryResult result = eventually(invokeT(elasticSearch.search("category", "some_type"), searchRequest, SampleCategoryResult.class));
        assertThat(result.getTamedOut()).isFalse();
        assertThat(result.getHits().getTotal()).isEqualTo(2);
        assertThat(result.getHits().getHits()).hasSize(2);
        assertThat(result.getHits().getHits().get(0).getSource().getName()).containsExactly("test1");
    }

    @Test
    @DisplayName("successfully perform BoolQuery search")
    void searchBoolQuery() throws InterruptedException, ExecutionException, TimeoutException {
        UserKeywordTerm userTerm = new UserKeywordTerm("user-9");
        MessageKeywordTerm messageTerm = new MessageKeywordTerm("message-9");
        SearchRequest request = new SearchRequest(
                BoolQuery.boolQuery()
                        .must(asList(TermQuery.ofTerm(userTerm), TermQuery.ofTerm(messageTerm)))
                        .build(),
                0, 100
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search("test", "sample"), request, SampleDocumentResult.class));
        List<HitResult<SampleDocument>> found = result.getHits().getHits().stream().filter(
                h -> h.getSource().getUser().equals(userTerm.getUser()) && h.getSource().getMessage().equals(messageTerm.getMessage())
        ).collect(Collectors.toList());
        assertThat(found).hasSize(1);
    }
}