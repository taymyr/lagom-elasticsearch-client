package org.taymyr.lagom.elasticsearch.search.dsl.query.suggest

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Fuzzy

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-suggesters-completion.html)
 */
data class CompletionSuggest(
    val prefix: String,
    val completion: Completion
) : Suggest {

    data class Completion(
        val field: String,
        val fuzzy: Fuzzy? = null,
        @JsonProperty("skip_duplicates")
        val skipDuplicates: Boolean = false
    )
}
