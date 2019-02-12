package org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.Order;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.IdsQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

class AggregationTest {

    @Test
    @DisplayName("successfully serialize search request with aggregation")
    void shouldSuccessfullySerializeSearchRequestWithAggregation() {
        SearchRequest request = SearchRequest.builder()
                .query(IdsQuery.of("1", "2"))
                .agg("filterAggregation", FilterAggregation.builder()
                        .filter(IdsQuery.of("3", "4"))
                        .agg("nestedAggregation", NestedAggregation.builder()
                                .nested("path")
                                .agg("termsAggregation1", TermsAggregation.builder()
                                        .field("field1")
                                        .order(Order.desc("field1"))
                                        .size(1)
                                        .agg("termsAggregation2", TermsAggregation.builder()
                                                .field("field2")
                                                .order(Order.asc("field2"))
                                                .build()
                                        ).build()
                                ).build()
                        ).build()
                )
                .agg("compositeAggregation", CompositeAggregation.builder()
                        .source(CompositeAggregation.sourceBuilder()
                                .agg("agg1", TermsAggregation.builder().field("field1").build())
                                .agg("agg2", TermsAggregation.builder().field("field2").build())
                        ).build()
                )
                .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/aggregation/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect aggregation")
    void shouldThrowExceptionForIncorrectAggregation() {
        assertThatThrownBy(() -> TermsAggregation.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'field' can't be null");
        assertThatThrownBy(() -> NestedAggregation.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'nested' can't be null");
        assertThatThrownBy(() -> NestedAggregation.builder().nested("nested").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'aggs' can't be empty");
        assertThatThrownBy(() -> FilterAggregation.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'filter' can't be null");
        assertThatThrownBy(() -> FilterAggregation.builder().filter(IdsQuery.of("1")).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'aggs' can't be empty");
        assertThatThrownBy(() -> CompositeAggregation.builder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("CompositeAggregation can't be empty");
        assertThatThrownBy(() -> CompositeAggregation.builder().source(CompositeAggregation.sourceBuilder()).build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Field 'aggs' can't be empty");
    }
}