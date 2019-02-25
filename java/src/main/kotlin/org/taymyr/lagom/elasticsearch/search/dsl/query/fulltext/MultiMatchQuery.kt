package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MultiMatchQueryType.BEST_FIELDS

data class MultiMatchQuery(
    @JsonProperty("multi_match")
    val multiMatch: MultiMatch
) : Query {

    class Builder {
        private var query: String? = null
        private var fields: MutableList<String> = mutableListOf()
        private var type: MultiMatchQueryType = BEST_FIELDS

        fun query(query: String) = apply { this.query = query }
        fun fields(vararg fields: String) = apply { this.fields.addAll(fields) }
        fun fields(fields: List<String>) = apply { this.fields.addAll(fields) }
        fun field(field: String) = apply { this.fields.add(field) }
        fun field(field: String, boost: Int) = apply { this.fields.add("$field^$boost") }
        fun type(type: MultiMatchQueryType) = apply { this.type = type }

        fun build() = MultiMatchQuery(
            MultiMatch(
                query = query ?: error("Field 'query' can't be null"),
                fields = if (fields.isEmpty()) error("Fields can't be empty") else fields.toList(),
                type = type
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}