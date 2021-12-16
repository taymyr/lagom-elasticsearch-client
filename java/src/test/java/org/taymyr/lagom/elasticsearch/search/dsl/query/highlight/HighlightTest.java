package org.taymyr.lagom.elasticsearch.search.dsl.query.highlight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchQuery;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

public class HighlightTest {

    @Test
    @DisplayName("successfully serialize search request with highlight")
    void shouldSuccessfullySerializeHighlight() {
        SearchRequest request = SearchRequest.builder()
            .query(MatchQuery.of("name", "value"))
            .highlight(
                Highlight.builder()
                    .boundaryChars("qwerty")
                    .boundaryMaxScan(20)
                    .boundaryScanner(BoundaryScanner.SENTENCE)
                    .boundaryScannerLocale(Locale.CANADA)
                    .encoder(Encoder.HTML)
                    .forceSource(true)
                    .fragmenter(Fragmenter.SPAN)
                    .fragmentOffset(12)
                    .fragmentSize(1024)
                    .matchedFields(Arrays.asList("field1", "field2"))
                    .noMatchSize(11)
                    .numberOfFragments(3)
                    .order("field3")
                    .phraseLimit(101)
                    .preTags(Arrays.asList("<h1>", "<h2>"))
                    .postTags(Arrays.asList("</h1>", "</h2>"))
                    .requireFieldMatch(false)
                    .tagsSchema(TagsSchema.STYLED)
                    .type(Type.UNIFIED)
                    .fields(
                        new HashMap<String, Highlight.Field>() {{
                            put("field4", Highlight.Field.builder()
                                .boundaryChars("qwerty")
                                .boundaryMaxScan(21)
                                .boundaryScanner(BoundaryScanner.WORD)
                                .boundaryScannerLocale(Locale.US)
                                .encoder(Encoder.DEFAULT)
                                .forceSource(true)
                                .fragmenter(Fragmenter.SIMPLE)
                                .fragmentOffset(13)
                                .fragmentSize(1023)
                                .matchedFields(Arrays.asList("field5", "field6"))
                                .noMatchSize(10)
                                .numberOfFragments(4)
                                .order("field7")
                                .phraseLimit(101)
                                .preTags(Collections.singletonList("<p>"))
                                .postTags(Collections.singletonList("</p>"))
                                .requireFieldMatch(true)
                                .tagsSchema(TagsSchema.STYLED)
                                .type(Type.FVH)
                                .build());
                        }}
                    )
                    .build()
            )
            .build();
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/highlight/request.json");
        assertThatJson(actual).isEqualTo(expected);
    }
}
