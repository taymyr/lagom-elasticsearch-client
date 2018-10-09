package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

data class BulkResult(val errors: Boolean, val items: List<BulkCommandResult>) {

    @JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
    @JsonSubTypes(
        Type(BulkCreateResult::class, name = "create"),
        Type(BulkIndexResult::class, name = "index"),
        Type(BulkUpdateResult::class, name = "update"),
        Type(BulkDeleteResult::class, name = "delete")
    )
    abstract class BulkCommandResult {
        @JsonProperty("_index") lateinit var index: String
        @JsonProperty("_type") lateinit var type: String
        @JsonProperty("_id") lateinit var id: String
        val status: Long = -1
        val result: String? = null
        val error: ResultItemError? = null

        data class ResultItemError(val type: String, val reason: String)
    }

    class BulkCreateResult : BulkCommandResult()
    class BulkIndexResult : BulkCommandResult()
    class BulkUpdateResult : BulkCommandResult()
    class BulkDeleteResult : BulkCommandResult()
}