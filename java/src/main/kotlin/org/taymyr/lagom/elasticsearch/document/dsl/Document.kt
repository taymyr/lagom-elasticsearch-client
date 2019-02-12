package org.taymyr.lagom.elasticsearch.document.dsl

import com.fasterxml.jackson.annotation.JsonProperty

open class Document<T> @JvmOverloads constructor(
    @get:JsonProperty("_source")
    var source: T? = null,
    @get:JsonProperty("_index")
    var index: String = "",
    @get:JsonProperty("_type")
    var type: String = "",
    @get:JsonProperty("_id")
    var id: String = "",
    @get:JsonProperty("_version")
    var version: Int = -1,
    @get:JsonProperty("found")
    var isFound: Boolean = false
)