package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MultiMatchQueryType.BEST_FIELDS

data class MultiMatchQuery(
    @JsonProperty("multi_match")
    val multiMatch: MultiMatch
) : Query {

    companion object {

        @JvmStatic
        fun of(multiMatch: MultiMatch) = MultiMatchQuery(multiMatch)

        @JvmStatic
        @JvmOverloads
        fun of(query: String, fieldsMap: Map<String, Int>, type: MultiMatchQueryType = BEST_FIELDS) =
            MultiMatchQuery(MultiMatch(query, fieldsMap.map { "${it.key}^${it.value}" }, type))

        @JvmStatic
        @JvmOverloads
        fun of(query: String, fields: List<String>, type: MultiMatchQueryType = BEST_FIELDS) =
            MultiMatchQuery(MultiMatch(query, fields.toMutableList(), type))
    }
}