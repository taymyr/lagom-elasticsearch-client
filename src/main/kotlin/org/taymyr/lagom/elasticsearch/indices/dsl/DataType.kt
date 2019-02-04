package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonValue

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html)
 */
enum class DataType(@JsonValue val title: String) {
    LONG("long"),
    TEXT("text"),
    DATE("date"),
    NESTED("nested"),
    OBJECT("object"),
    KEYWORD("keyword"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    DOUBLE("double"),
    COMPLETION("completion")
}