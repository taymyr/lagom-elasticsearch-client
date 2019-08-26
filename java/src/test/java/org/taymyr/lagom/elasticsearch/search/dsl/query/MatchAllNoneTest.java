package org.taymyr.lagom.elasticsearch.search.dsl.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.MatchAllQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;
import static org.taymyr.lagom.elasticsearch.search.dsl.query.Query.MATCH_ALL;

class MatchAllNoneTest {

    @Test
    @DisplayName("successfully serialize search request with match_all and match_none")
    void shouldSuccessfullySerializeMatchAllNone() {
        SearchRequest request = SearchRequest.builder()
                .query(BoolQuery.builder()
                        .filter(MATCH_ALL)
                        .filter(MatchAllQuery.of(1.0))
                        .filter(Query.MATCH_NONE)
                        .build())
                .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/match_all_none.json");
        assertThatJson(actual).isEqualTo(expected);
    }
}
