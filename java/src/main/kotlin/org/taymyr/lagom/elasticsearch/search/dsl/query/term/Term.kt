package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class Term @JvmOverloads constructor(val value: String, val boost: Double? = null)