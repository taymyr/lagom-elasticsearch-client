package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-custom-analyzer.html).
 */
data class CustomAnalyzer @JvmOverloads constructor(
    val tokenizer: String? = null,
    val filter: List<String>? = null
) : Analyzer("custom")
