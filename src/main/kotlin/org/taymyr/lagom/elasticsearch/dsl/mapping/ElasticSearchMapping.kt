package org.taymyr.lagom.elasticsearch.dsl.mapping

import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
data class ElasticSearchMapping(
    val properties: Map<String, MappingProperty>
) {

    fun withTypeName(typeName: String) = mapOf(typeName to this)
}
