package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-params.html)
 */
data class MappingProperty(
    val type: String,
    val analyzer: String? = null,
    val properties: Map<String, MappingProperty>? = null
) {
    constructor(type: DataType, analyzer: String? = null, properties: Map<String, MappingProperty>? = null) : this(type.title, analyzer, properties)

    class Builder {
        private var type: String? = null
        private var analyzer: String? = null
        private var properties: Map<String, MappingProperty>? = null

        fun type(type: String) = apply { this.type = type }
        fun analyzer(analyzer: String) = apply { this.analyzer = analyzer }
        fun properties(properties: Map<String, MappingProperty>) = apply { this.properties = properties }

        fun build() = MappingProperty(type ?: error("Type can't be null"), analyzer, properties)
    }

    companion object {
        @JvmStatic val LONG = MappingProperty(DataType.LONG)
        @JvmStatic val TEXT = MappingProperty(DataType.TEXT)
        @JvmStatic val DATE = MappingProperty(DataType.DATE)
        @JvmStatic val NESTED = MappingProperty(DataType.NESTED)
        @JvmStatic val OBJECT = MappingProperty(DataType.OBJECT)
        @JvmStatic val INTEGER = MappingProperty(DataType.INTEGER)
        @JvmStatic val KEYWORD = MappingProperty(DataType.KEYWORD)
        @JvmStatic val BOOLEAN = MappingProperty(DataType.BOOLEAN)

        @JvmStatic fun mapping() = MappingProperty.Builder()
    }
}