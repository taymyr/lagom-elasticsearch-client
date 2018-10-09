package org.taymyr.lagom.elasticsearch.indices.dsl

enum class MappingTypes(val title: String) {
    LONG("long"),
    TEXT("text"),
    OBJECT("object"),
    BOOLEAN("boolean")
}