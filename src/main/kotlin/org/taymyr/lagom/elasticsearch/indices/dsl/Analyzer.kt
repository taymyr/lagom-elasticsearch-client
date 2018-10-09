package org.taymyr.lagom.elasticsearch.indices.dsl

abstract class Analyzer {
    abstract val type: String
    open val tokenizer: String? = null
    open val filter: List<String>? = null
}