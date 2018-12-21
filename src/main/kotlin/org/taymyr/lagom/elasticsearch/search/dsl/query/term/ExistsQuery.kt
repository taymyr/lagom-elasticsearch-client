package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-exists-query.html)
 */
data class ExistsQuery(val exists: Exists) : TermLevelQuery {

    data class ExistsField(val field: String?)

    class ExistsQueryBuilder {

        private var field: String? = null

        fun field(field: String?) = apply { this.field = field }

        fun build(): ExistsField {
            if (field == null) {
                throw error("\"Exists\" query must have not null parameter \"field\"")
            }
            return ExistsField(field)
        }
    }

    companion object {
        @JvmStatic fun Exists() = ExistsQueryBuilder()
        @JvmStatic fun ofExists(exists: Exists) = ExistsQuery(exists)
    }
}