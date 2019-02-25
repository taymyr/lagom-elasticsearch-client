package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-exists-query.html)
 */
data class ExistsQuery(val exists: Exists) : TermLevelQuery {

    companion object {
        @JvmStatic
        fun of(field: String) = ExistsQuery(Exists(field))
    }
}