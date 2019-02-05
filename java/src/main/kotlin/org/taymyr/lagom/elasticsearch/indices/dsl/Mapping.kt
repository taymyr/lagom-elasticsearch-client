package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
data class Mapping @JvmOverloads constructor(
    val properties: Map<String, MappingProperty>,
    val dynamic: DynamicType? = null
)