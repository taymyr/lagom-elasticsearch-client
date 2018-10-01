package org.taymyr.lagom.elasticsearch.dsl.mapping

enum class MappingTypes(val title: String) {
    LONG("long"),
    TEXT("text"),
    OBJECT("object"),
    BOOLEAN("boolean")
}
