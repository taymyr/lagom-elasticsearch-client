package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * @author Ilya Korshunov
 */
data class MappingProperty(
    val type: String,
    val analyzer: String? = null
) {
    constructor(type: MappingTypes, analyzer: String? = null) : this(type.title, analyzer)
}