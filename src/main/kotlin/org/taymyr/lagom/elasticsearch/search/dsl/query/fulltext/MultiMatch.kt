package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

data class MultiMatch(
    val query: String,
    val fields: List<String>
) {

    companion object {

        /**
         * Creates multi match query with boosted fields.
         */
        @JvmStatic fun of(query: String, vararg fields: Pair<String, Int>): MultiMatch {
            return MultiMatch(
                query,
                fields.map { "${it.first}^${it.second}" }
            )
        }
    }
}
