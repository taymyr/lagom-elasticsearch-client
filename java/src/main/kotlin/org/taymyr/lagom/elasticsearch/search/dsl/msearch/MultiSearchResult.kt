package org.taymyr.lagom.elasticsearch.search.dsl.msearch

import com.fasterxml.jackson.databind.JsonNode
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult

data class MultiSearchResult @JvmOverloads constructor(
    val took: Long? = null,
    val responses: List<JsonNode> = emptyList()
) {

    /**
     * The status may remain '200' even if one of the searches fails.
     */
    fun isError(index: Int): Boolean = responses[index].has("error")

    fun status(index: Int): Int = responses[index].path("status").asInt(200)

    fun error(index: Int): JsonNode? = responses[index].get("error")

    /**
     * Converts the response of the search with the given index to the typed result
     *
     * @throws IllegalStateException if the search has failed
     */
    fun <R : SearchResult<*>> result(index: Int, type: Class<R>): R {
        val node = responses[index]
        check(!node.has("error")) { "_msearch item $index failed: ${node.get("error")}" }
        val reader = ElasticSerializerFactory.MAPPER.readerFor(type)
        val result: R = reader.readValue(node)
        result.initialize(node, reader)
        return result
    }
}
