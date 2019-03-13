package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/suggester-context.html)
 */
data class Context @JvmOverloads constructor(
    val name: String,
    val type: ContextType,
    val path: String? = null
) {

    class Builder {
        private var name: String? = null
        private var type: ContextType? = null
        private var path: String? = null

        fun name(name: String) = apply { this.name = name }
        fun type(type: ContextType) = apply { this.type = type }
        fun path(path: String) = apply { this.path = path }

        fun build() = Context(name ?: error("Field 'name' can not be null"), type ?: error("Field 'type' can not be null"), path)
    }

    companion object {
        @JvmStatic
        fun builder() = Context.Builder()
    }
}