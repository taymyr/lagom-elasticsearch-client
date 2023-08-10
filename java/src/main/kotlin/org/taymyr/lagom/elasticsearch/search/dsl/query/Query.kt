package org.taymyr.lagom.elasticsearch.search.dsl.query

interface Query {

    companion object {
        @JvmField
        val MATCH_ALL = MatchAllQuery()

        @JvmField
        val MATCH_NONE = MatchNoneQuery()
    }
}
