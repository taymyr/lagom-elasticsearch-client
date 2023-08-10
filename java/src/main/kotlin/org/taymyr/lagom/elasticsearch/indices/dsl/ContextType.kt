package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonValue

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/suggester-context.html)
 */
enum class ContextType(@JsonValue val type: String) {
    CATEGORY("category")
}
