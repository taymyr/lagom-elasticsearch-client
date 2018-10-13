package org.taymyr.lagom.elasticsearch.indices.dsl

data class MappingProperty(
    val type: String,
    val analyzer: String? = null,
    val properties: Map<String, MappingProperty>? = null
) {
    constructor(type: DataType, analyzer: String? = null, properties: Map<String, MappingProperty>? = null) : this(type.title, analyzer, properties)
    companion object {
        @JvmStatic val LONG = MappingProperty(DataType.LONG)
        @JvmStatic val TEXT = MappingProperty(DataType.TEXT)
        @JvmStatic val DATE = MappingProperty(DataType.DATE)
        @JvmStatic val NESTED = MappingProperty(DataType.NESTED)
        @JvmStatic val OBJECT = MappingProperty(DataType.OBJECT)
        @JvmStatic val INTEGER = MappingProperty(DataType.INTEGER)
        @JvmStatic val KEYWORD = MappingProperty(DataType.KEYWORD)
        @JvmStatic val BOOLEAN = MappingProperty(DataType.BOOLEAN)
    }
}