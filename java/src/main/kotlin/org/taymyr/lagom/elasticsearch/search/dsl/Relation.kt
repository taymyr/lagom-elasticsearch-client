package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonValue

enum class Relation(@JsonValue private val title: String) {
    EQUAL("eq"),
    GREATER("gte")
}
