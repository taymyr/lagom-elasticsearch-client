package org.taymyr.lagom.elasticsearch.search.dsl

data class Hits<T>(val hits: List<HitResult<T>>, val total: Int)