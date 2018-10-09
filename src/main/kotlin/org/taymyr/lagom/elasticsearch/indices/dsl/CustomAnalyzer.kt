package org.taymyr.lagom.elasticsearch.indices.dsl

data class CustomAnalyzer(
    override val type: String,
    override val tokenizer: String?,
    override val filter: List<String>?
) : Analyzer()