package org.taymyr.lagom.elasticsearch.indices.dsl

data class MappingProperty(
    val type: String,
    val analyzer: String? = null
) {
    constructor(type: MappingTypes, analyzer: String? = null) : this(type.title, analyzer)
}