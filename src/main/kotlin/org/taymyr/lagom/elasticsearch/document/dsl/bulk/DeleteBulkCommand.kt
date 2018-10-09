package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("delete")
data class DeleteBulkCommand(
    override val id: String
) : BulkCommand(id)