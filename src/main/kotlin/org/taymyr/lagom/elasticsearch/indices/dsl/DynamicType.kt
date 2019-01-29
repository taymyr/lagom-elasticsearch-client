package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonValue

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic.html)
 */
enum class DynamicType(@JsonValue val value: String) {
    TRUE("true"),
    FALSE("false"),
    STRICT("strict")
}