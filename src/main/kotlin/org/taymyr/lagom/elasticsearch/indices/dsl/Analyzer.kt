package org.taymyr.lagom.elasticsearch.indices.dsl

data class Analyzer(
    val type: String,
    val tokenizer: String?,
    val filter: List<String>?
)