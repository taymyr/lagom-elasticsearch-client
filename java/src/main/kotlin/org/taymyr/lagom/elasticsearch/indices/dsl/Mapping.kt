package org.taymyr.lagom.elasticsearch.indices.dsl

data class Mapping @JvmOverloads constructor(
    val properties: Map<String, MappingProperty>,
    val dynamic: DynamicType? = null
)
