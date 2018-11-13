package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty

data class MultiMatchSimple(
    val query: String,
    @JsonProperty("type")
    val queryType: String? = MultiMatchQueryType.BEST_FIELDS.title,
    val fields: List<String>
) : MultiMatch {

    class MultiMatchSimpleBuilder {
        protected var query: String? = null
        protected var queryType: String? = null
        protected var fields: List<String>? = listOf()

        fun query(query: String) = apply { this.query = query }
        fun queryType(queryType: String) = apply { this.queryType = queryType }
        fun fields(fields: List<String>) = apply { this.fields = fields.toMutableList() }
        // specify field priority as map values, if all are undefined- use fields method instead
        fun fieldsMap(fieldsMap: Map<String, Int>) = apply { fieldsMap.map { "${it.key}^${it.value}" } }

        fun build(): MultiMatchSimple {
            return MultiMatchSimple(
                query = query ?: throw error("Multimatch query must have \"query\" field"),
                queryType = queryType ?: MultiMatchQueryType.BEST_FIELDS.title,
                fields = fields ?: throw error("Multimatch query must have list of document fields in \"fields\" query field")
            )
        }
    }
}