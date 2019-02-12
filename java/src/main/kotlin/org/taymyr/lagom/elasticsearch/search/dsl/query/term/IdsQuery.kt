package org.taymyr.lagom.elasticsearch.search.dsl.query.term

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-ids-query.html).
 */
data class IdsQuery(val ids: Ids) : TermLevelQuery {

    class Builder {
        private var values: MutableList<String> = mutableListOf()
        private var types: MutableList<String> = mutableListOf()

        fun values(vararg values: String) = apply { this.values.addAll(values) }
        fun type(type: String) = apply { this.types.add(type) }
        fun types(vararg types: String) = apply { this.types.addAll(types) }

        fun build() = IdsQuery(Ids(
            values = if (values.isNotEmpty()) values.toList() else error("Values can't be empty"),
            type = if (types.isNotEmpty()) types.toList() else null
        ))
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun of(vararg values: String) = IdsQuery(Ids(values = values.toList()))
    }
}