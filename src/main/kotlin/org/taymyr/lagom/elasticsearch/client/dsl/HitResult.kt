package org.taymyr.lagom.elasticsearch.client.dsl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.client.DTOAnnotation
import org.taymyr.lagom.elasticsearch.client.Indexable

@DTOAnnotation
@JsonIgnoreProperties(ignoreUnknown = true)
data class HitResult<T: Indexable> @JsonCreator constructor(
        @JsonProperty("_score") val score: Double,
        @JsonProperty("_source") val item: T
)
