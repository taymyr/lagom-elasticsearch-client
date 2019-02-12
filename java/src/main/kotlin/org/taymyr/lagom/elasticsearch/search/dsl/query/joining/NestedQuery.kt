package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html)
 */
data class NestedQuery(val nested: NestedQueryBody) : JoiningQuery {

    class Builder {
        private var path: String? = null
        private var query: Query? = null
        private var scoreMode: NestedQueryScoreMode? = null

        fun path(path: String) = apply { this.path = path }
        fun query(query: Query) = apply { this.query = query }
        fun scoreMode(scoreMode: NestedQueryScoreMode) = apply { this.scoreMode = scoreMode }

        fun build() = NestedQuery(
            NestedQueryBody(
                path = path ?: error("Field 'path' can't be null"),
                query = query ?: error("Field 'query' can't be null"),
                scoreMode = scoreMode
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}