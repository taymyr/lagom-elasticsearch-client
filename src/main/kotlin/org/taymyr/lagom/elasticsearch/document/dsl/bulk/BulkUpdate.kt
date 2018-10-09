package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonIgnore
import org.taymyr.lagom.elasticsearch.document.dsl.Document

data class BulkUpdate(
    override val id: String,
    @JsonIgnore
    val element: Document<*>
) : BulkCommand(id)
