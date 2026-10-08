package org.taymyr.lagom.elasticsearch.search.dsl.msearch

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/search-multi-search.html)
 * Serialized as NDJSON: header line and body line for every search.
 */
data class MultiSearchRequest(val searches: List<MultiSearchItem>) {
    init {
        require(searches.isNotEmpty()) { "_msearch requires at least one search" }
    }
    companion object {
        @JvmStatic
        fun of(searches: List<MultiSearchItem>) = MultiSearchRequest(searches)

        @JvmStatic
        fun of(vararg searches: MultiSearchItem) = MultiSearchRequest(searches.asList())
    }
}

data class MultiSearchHeader @JvmOverloads constructor(
    val index: List<String>? = null,
    val routing: String? = null,
    val preference: String? = null,
    @field:JsonProperty("request_cache")
    val requestCache: Boolean? = null
) {
    companion object {
        @JvmStatic
        fun of(vararg index: String) = MultiSearchHeader(index = index.asList())
    }
}

data class MultiSearchItem @JvmOverloads constructor(
    val body: SearchRequest,
    val header: MultiSearchHeader = MultiSearchHeader()
) {
    companion object {
        @JvmStatic
        fun of(index: String, body: SearchRequest) = MultiSearchItem(body, MultiSearchHeader.of(index))
    }
}
