package org.taymyr.lagom.elasticsearch.document.dsl.bulk

data class BulkDelete(override val id: String) : BulkCommand(id)