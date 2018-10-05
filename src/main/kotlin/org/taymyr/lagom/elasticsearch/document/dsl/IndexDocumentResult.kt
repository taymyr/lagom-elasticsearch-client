package org.taymyr.lagom.elasticsearch.document.dsl

import com.fasterxml.jackson.annotation.JsonProperty
import javax.annotation.concurrent.Immutable

@Immutable
data class IndexDocumentResult(
    @JsonProperty("_index") val index: String,
    @JsonProperty("_type") val type: String
)