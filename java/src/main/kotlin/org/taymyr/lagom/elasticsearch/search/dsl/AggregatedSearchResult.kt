package org.taymyr.lagom.elasticsearch.search.dsl

abstract class AggregatedSearchResult<T, AT> : SearchResult<T>() {
    val aggregations: AT? = null
}
