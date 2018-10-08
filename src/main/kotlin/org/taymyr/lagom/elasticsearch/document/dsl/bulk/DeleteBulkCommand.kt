package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonTypeName

/**
 * @author Ilya Korshunov
 */
@JsonTypeName("delete")
data class DeleteBulkCommand(
    override val id: String
) : BulkCommand(id)