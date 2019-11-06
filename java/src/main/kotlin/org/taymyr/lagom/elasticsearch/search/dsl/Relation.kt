package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonValue

enum class Relation(@JsonValue val title: String) {
    EQ("eq"),
    GTE("gte")
}
