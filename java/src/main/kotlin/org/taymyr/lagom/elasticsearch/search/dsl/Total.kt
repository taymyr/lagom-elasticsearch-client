package org.taymyr.lagom.elasticsearch.search.dsl

data class Total(val value: Int = 0, val relation: String = "eq")
