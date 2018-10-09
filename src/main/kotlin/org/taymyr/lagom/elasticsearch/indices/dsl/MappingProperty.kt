package org.taymyr.lagom.elasticsearch.indices.dsl

data class MappingProperty(
    val type: String,
    val analyzer: String? = null
) {
    constructor(type: DataType, analyzer: String? = null) : this(type.title, analyzer)
    companion object {
        @JvmStatic val LONG = MappingProperty(DataType.LONG)
        @JvmStatic val TEXT = MappingProperty(DataType.TEXT)
        @JvmStatic val OBJECT = MappingProperty(DataType.OBJECT)
        @JvmStatic val BOOLEAN = MappingProperty(DataType.BOOLEAN)
    }
}