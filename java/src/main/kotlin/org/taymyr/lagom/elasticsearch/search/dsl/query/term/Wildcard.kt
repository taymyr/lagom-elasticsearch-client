package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class Wildcard @JvmOverloads constructor(val value: String, val boost: Double? = null)
