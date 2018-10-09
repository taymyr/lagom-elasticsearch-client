package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeName
import org.taymyr.lagom.elasticsearch.document.dsl.Document

@JsonTypeName("index")
data class IndexBulkCommand(
    override val id: String,
    @JsonIgnore
    val element: Document<*>
) : BulkCommand(id)