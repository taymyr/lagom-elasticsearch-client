package org.taymyr.lagom.elasticsearch.script

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-scripting-using.html).
 */
data class Script @JvmOverloads constructor(
    val id: String? = null,
    val source: String? = null,
    val lang: String? = null,
    val params: Map<String, Any?>? = null
) {

    class Builder {
        private var id: String? = null
        private var source: String? = null
        private var lang: String? = null
        private var params: MutableMap<String, Any?> = mutableMapOf()

        fun id(id: String) = apply { this.id = id }
        fun source(source: String) = apply { this.source = source }
        fun lang(lang: String) = apply { this.lang = lang }
        fun param(name: String, value: Any?) = apply { this.params[name] = value }
        fun params(params: Map<String, Any?>) = apply { this.params = params.toMutableMap() }

        fun build() =
            if (id == null && source == null) error("Field 'source' or 'id' can't be null")
            else Script(id, source, lang, params)
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun of(source: String) = Script(source = source)
    }
}
