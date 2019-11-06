package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectReader

abstract class SearchResult<T> {
    val hits: Hits<T> = Hits(listOf(), Total())
    val took: Int = -1
    @JsonProperty("timed_out")
    val tamedOut: Boolean = false
    val suggest: Map<String, List<SuggestResult<T>>>? = null

    val sources: List<T>
        @JsonIgnore get() = hits.hits.map { it.source }

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
