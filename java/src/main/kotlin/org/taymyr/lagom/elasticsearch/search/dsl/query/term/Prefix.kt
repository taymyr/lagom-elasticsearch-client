package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class Prefix @JvmOverloads constructor(val value: String, val boost: Double? = null)