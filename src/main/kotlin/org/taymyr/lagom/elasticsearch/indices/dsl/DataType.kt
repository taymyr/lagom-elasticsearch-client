package org.taymyr.lagom.elasticsearch.indices.dsl

enum class DataType(val title: String) {
    LONG("long"),
    TEXT("text"),
    OBJECT("object"),
    BOOLEAN("boolean")
}