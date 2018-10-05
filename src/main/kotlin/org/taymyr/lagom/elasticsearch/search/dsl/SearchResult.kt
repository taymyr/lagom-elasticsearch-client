package org.taymyr.lagom.elasticsearch.search.dsl

abstract class SearchResult<T> {
    abstract val hits: Hits<T>
    val took: Int = -1
    val tamedOut: Boolean = false
}