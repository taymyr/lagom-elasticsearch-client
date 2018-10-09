package org.taymyr.lagom.elasticsearch.document.dsl.bulk

data class BulkRequest(
    val commands: List<BulkCommand>
)
