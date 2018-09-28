package org.taymyr.lagom.elasticsearch.client.dsl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.client.DTOAnnotation
import org.taymyr.lagom.elasticsearch.client.Indexable

@DTOAnnotation
data class UpdateIndexItem<T: Indexable> @JsonCreator constructor(
        val doc: T,
        @JsonProperty("doc_as_upsert") val docAsUpsert: Boolean = true
)
