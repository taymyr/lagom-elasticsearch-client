package org.taymyr.lagom.elasticsearch.search;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.MessageMatchPhrase;
import org.taymyr.lagom.elasticsearch.MessageMatchPhrasePrefix;
import org.taymyr.lagom.elasticsearch.MessagePrefix;
import org.taymyr.lagom.elasticsearch.MessageRegexp;
import org.taymyr.lagom.elasticsearch.MessageWildcard;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocumentResult;
import org.taymyr.lagom.elasticsearch.UserKeywordTerm;
import org.taymyr.lagom.elasticsearch.UserMatchPhrase;
import org.taymyr.lagom.elasticsearch.UserMatchPhrasePrefix;
import org.taymyr.lagom.elasticsearch.UserPrefix;
import org.taymyr.lagom.elasticsearch.document.dsl.IndexDocumentResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrasePrefixQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhraseQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.PrefixQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.RegexpQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.TermQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.WildcardQuery;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invoke;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.KEYWORD;
import static org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty.TEXT;
import static org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery.boolQuery;

import static java.lang.Thread.sleep;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Testing full-test searching, including:
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhraseQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrasePrefixQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.RegexpQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.WildcardQuery]
 * * [org.taymyr.lagom.elasticsearch.search.dsl.query.term.PrefixQuery]
 */
class ElasticSearchFullTextIT extends AbstractElasticsearchIT {

    private static String indexName = "full-text-test";

    @BeforeAll
    static void createIndexAndData() throws InterruptedException, ExecutionException, TimeoutException {
        String typeName = "samples";
        CreateIndex request = new CreateIndex(
                new Settings(1, 1),
                ImmutableMap.of(
                        typeName, new Mapping(ImmutableMap.of(
                                "user", KEYWORD,
                                "message", TEXT
                        ))
                )
        );
        CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(request));
        assertThat(result.getIndex()).isEqualTo(indexName);
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();

        for (int it = 0; it < 20; ++it) {
            String uuid = it + UUID.randomUUID().toString();
            String symbol = (it % 10 == 3) ? uuid : "";
            SampleDocument doc = new SampleDocument(
                    "fullTextUser" + uuid,
                    "random" + symbol + " generated message " + uuid + " for full text search",
                    null,
                    null,
                    now().truncatedTo(DAYS)
            );
            IndexDocumentResult r = eventually(invoke(elasticDocument.indexWithId(indexName, typeName, uuid), doc));
            assertThat(r.getIndex()).isEqualTo(indexName);
            assertThat(r.getType()).isEqualTo(typeName);
        }
        sleep(1_000);
    }

    @Test
    @DisplayName("find none documents with keyword 'user' term 'fullTextUser'")
    void test1() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().must(new TermQuery(new UserKeywordTerm("fullTextUser"))).build()
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).isEmpty();
    }

    @Test
    @DisplayName("find none documents with keyword 'user' match-phrase 'fullTextUser'")
    void test2() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new MatchPhraseQuery(new UserMatchPhrase("fullTextUser"))).build()
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).isEmpty();
    }

    @Test
    @DisplayName("successfully find all documents with keyword 'user' match-phrase-prefix 'fullTextUser'")
    void test3() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new MatchPhrasePrefixQuery(new UserMatchPhrasePrefix("fullTextUser"))).build(),
                null,
                25
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(20);
    }

    @Test
    @DisplayName("successfully find all documents with keyword 'user' prefix 'full'")
    void test4() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new PrefixQuery(new UserPrefix("full"))).build(),
                null,
                25
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(20);
    }

    @Test
    @DisplayName("successfully find all documents with text 'message' match-phrase 'generated'")
    void test5() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().must(new MatchPhraseQuery(new MessageMatchPhrase("generated"))).build(),
                null,
                25
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(20);
    }

    @Test
    @DisplayName("find none documents with text 'message' match-phrase 'random'")
    void test6() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new MatchPhraseQuery(new MessageMatchPhrase("random"))).build(),
                null,
                25
        );
        // Two record with `random${simbol}` message should not be found
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(18);
    }

    @Test
    @DisplayName("successfully find all documents with text 'message' match-phrase-prefix 'random'")
    void test7() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new MatchPhrasePrefixQuery(new MessageMatchPhrasePrefix("random"))).build(),
                null,
                25
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(20);
    }

    @Test
    @DisplayName("successfully find all documents text 'message' prefix 'random' that is for terms")
    void test8() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new PrefixQuery(new MessagePrefix("random"))).build(),
                null,
                25
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(20);
    }

    @Test
    @DisplayName("successfully find all documents text 'message' regexp 'random.+' that is for terms")
    void test9() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new RegexpQuery(new MessageRegexp("random.+"))).build()
        );
        // Two record with `random${simbol}` message should be found
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(2);
    }

    @Test
    @DisplayName("successfully find all documents text 'message' wildcard 'random?*' that is for terms")
    void test10() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                boolQuery().filter(new WildcardQuery(new MessageWildcard("random?*"))).build()
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(2);
    }
}