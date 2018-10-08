package org.taymyr.lagom.elasticsearch.document.dsl.bulk

/**
 * @author Ilya Korshunov
 */
data class BulkRequest(
    val commands: List<BulkCommand>
)
