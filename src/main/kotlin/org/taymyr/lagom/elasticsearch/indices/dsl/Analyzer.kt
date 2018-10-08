package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * @author Ilya Korshunov
 */
data class Analyzer(
    val type: String,
    val tokenizer: String?,
    val filter: List<String>?
)