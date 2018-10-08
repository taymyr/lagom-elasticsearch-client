package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import org.taymyr.lagom.elasticsearch.document.dsl.Document

/**
 * @author Ilya Korshunov
 */
@JsonTypeName("update")
data class UpdateBulkCommand(
    override val id: String,
    @JsonIgnore
    val element: Document<*>,
    @JsonIgnore
    val ignoreNull: Boolean = true
) : BulkCommand(id)
