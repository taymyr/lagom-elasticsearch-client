package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-params.html)
 */
data class MappingProperty @JvmOverloads constructor(
    val type: DataType? = DataType.OBJECT,
    val format: String? = null,
    val analyzer: String? = null,
    val properties: Map<String, MappingProperty>? = null,
    val fields: Map<String, MappingProperty>? = null,
    val dynamic: DynamicType? = null,
    @JsonProperty("copy_to") val copyTo: List<String>? = null
) {

    class Builder {
        private var type: DataType? = null
        private var format: String? = null
        private var analyzer: String? = null
        private var properties: Map<String, MappingProperty>? = null
        private var fields: Map<String, MappingProperty>? = null
        private var dynamic: DynamicType? = null
        private var copyTo: List<String>? = null

        fun type(type: DataType) = apply { this.type = type }
        fun format(format: String) = apply { this.format = format }
        fun analyzer(analyzer: String) = apply { this.analyzer = analyzer }
        fun properties(properties: Map<String, MappingProperty>) = apply { this.properties = properties }
        fun fields(fields: Map<String, MappingProperty>) = apply { this.fields = fields }
        fun dynamic(dynamic: DynamicType) = apply { this.dynamic = dynamic }
        fun copyTo(copyTo: String) = apply { this.copyTo = listOf(copyTo) }
        fun copyTo(copyTo: List<String>) = apply { this.copyTo = copyTo }

        fun build() = MappingProperty(type ?: error("Type can't be null"), format, analyzer, properties, fields, dynamic, copyTo)
    }

    companion object {
        @JvmField val LONG = MappingProperty(DataType.LONG)
        @JvmField val TEXT = MappingProperty(DataType.TEXT)
        @JvmField val DATE = MappingProperty(DataType.DATE)
        @JvmField val NESTED = MappingProperty(DataType.NESTED)
        @JvmField val OBJECT = MappingProperty(DataType.OBJECT)
        @JvmField val INTEGER = MappingProperty(DataType.INTEGER)
        @JvmField val KEYWORD = MappingProperty(DataType.KEYWORD)
        @JvmField val BOOLEAN = MappingProperty(DataType.BOOLEAN)
        @JvmField val DOUBLE = MappingProperty(DataType.DOUBLE)
        @JvmField val COMPLETION = MappingProperty(DataType.COMPLETION)

        @JvmStatic fun mapping() = MappingProperty.Builder()
    }
}