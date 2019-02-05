package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-custom-analyzer.html).
 */
data class CustomAnalyzer(val tokenizer: String?, val filter: List<String>?) : Analyzer("custom")