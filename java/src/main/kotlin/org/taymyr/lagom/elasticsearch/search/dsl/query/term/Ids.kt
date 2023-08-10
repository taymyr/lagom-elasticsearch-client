package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class Ids @JvmOverloads constructor(val values: List<String>, val type: List<String>? = null)
