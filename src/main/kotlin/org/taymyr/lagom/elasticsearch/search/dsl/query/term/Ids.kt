package org.taymyr.lagom.elasticsearch.search.dsl.query.term

data class Ids(val values: List<String>, val type: String? = null)