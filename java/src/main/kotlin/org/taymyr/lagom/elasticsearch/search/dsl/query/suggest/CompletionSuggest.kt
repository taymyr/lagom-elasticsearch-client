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

    data class Completion @JvmOverloads constructor(
        val field: String,
        val fuzzy: Fuzzy? = null,
        val contexts: Map<String, String>? = null,
        @JsonProperty("skip_duplicates")
        val skipDuplicates: Boolean = false
    )

    class Builder {
        private var prefix: String? = null
        private var field: String? = null
        private var fuzzy: Fuzzy? = null
        private val contexts: MutableMap<String, String> = mutableMapOf()
        private var skipDuplicates: Boolean = false

        fun prefix(prefix: String) = apply { this.prefix = prefix }
        fun field(field: String) = apply { this.field = field }
        fun context(name: String, value: String) = apply { this.contexts[name] = value }
        fun fuzzy(fuzzy: Fuzzy) = apply { this.fuzzy = fuzzy }
        fun fuzzy(fuzzy: String) = apply { this.fuzzy = Fuzzy(fuzzy) }
        fun skipDuplicates(skipDuplicates: Boolean) = apply { this.skipDuplicates = skipDuplicates }

        fun build() = CompletionSuggest(
            prefix ?: error("Field 'prefix' can't be null"),
            Completion(
                field = field ?: error("Field 'field' can't be null"),
                contexts = if (contexts.isEmpty()) null else contexts,
                fuzzy = fuzzy,
                skipDuplicates = skipDuplicates
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
