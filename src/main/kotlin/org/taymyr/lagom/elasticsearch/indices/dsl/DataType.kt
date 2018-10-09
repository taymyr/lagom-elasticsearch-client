package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-types.html)
 */
enum class DataType(val title: String) {
    LONG("long"),
    TEXT("text"),
    OBJECT("object"),
    BOOLEAN("boolean")
}