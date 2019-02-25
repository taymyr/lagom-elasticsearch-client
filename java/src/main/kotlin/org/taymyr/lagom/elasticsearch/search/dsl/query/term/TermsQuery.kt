package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-terms-query.html)
 */
data class TermsQuery(val terms: Map<String, List<String>>) : TermLevelQuery {

    class Builder {
        private var terms: MutableMap<String, List<String>> = mutableMapOf()

        fun term(field: String, vararg values: String) = apply { this.terms[field] = values.toList() }
        fun term(field: String, values: List<String>) = apply { this.terms[field] = values }

        fun build() = TermsQuery(terms.toMap())
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun of(terms: Map<String, List<String>>) = TermsQuery(terms)
    }
}