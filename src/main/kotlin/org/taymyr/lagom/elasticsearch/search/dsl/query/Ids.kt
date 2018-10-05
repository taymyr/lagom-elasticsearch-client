package org.taymyr.lagom.elasticsearch.search.dsl.query

data class Ids(val values: List<String>, val type: String? = null)