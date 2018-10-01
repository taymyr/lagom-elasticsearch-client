package org.taymyr.lagom.elasticsearch.dsl.search

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.DTOAnnotation
import org.taymyr.lagom.elasticsearch.Indexable

@DTOAnnotation
@JsonIgnoreProperties(ignoreUnknown = true)
data class Hits<T : Indexable> @JsonCreator constructor(
    @JsonProperty("hits") val hits: List<HitResult<T>>,
    @JsonProperty("total") val total: Int
)
