package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectReader

abstract class SearchResult<T> {
    abstract val hits: Hits<T>
    val took: Int = -1
    @JsonProperty("timed_out")
    val tamedOut: Boolean = false

    /**
     * JSON root node of result for search query.
     */
    private lateinit var rootNode: JsonNode
    private lateinit var objectReader: ObjectReader

    fun <K> getTyped(path: String, type: Class<K>): K = objectReader.forType(type).readValue(rootNode.at(path))
    fun initialize(rootNode: JsonNode, objectReader: ObjectReader) {
        this.rootNode = rootNode
        this.objectReader = objectReader
    }
}
