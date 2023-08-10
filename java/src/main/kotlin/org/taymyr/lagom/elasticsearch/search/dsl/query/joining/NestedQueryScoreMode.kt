package org.taymyr.lagom.elasticsearch.search.dsl.query.joining

import com.fasterxml.jackson.annotation.JsonValue

enum class NestedQueryScoreMode(@JsonValue private val title: String) {
    AVG("avg"),
    SUM("sum"),
    MIN("min"),
    MAX("max"),
    NONE("none")
}
