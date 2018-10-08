package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
class CreateIndex(
    val settings: Settings? = null,
    val mappings: Map<String, Mapping>? = null
) {

    @Immutable
    data class Settings(
        val numberOfShards: Int?,
        val numberOfReplicas: Int?,
        val analysis: Analysis? = null
    )

    @Immutable
    data class Mapping(val properties: Map<String, MappingProperty>)

    @Immutable
    data class Analysis(
        val filter: Map<String, Filter>?,
        val analyzer: Map<String, Analyzer>?
    )
}
