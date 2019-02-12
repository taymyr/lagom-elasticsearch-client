package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

data class BulkResult(
    val took: Int,
    @JsonProperty("errors")
    val isErrors: Boolean,
    val items: List<BulkCommandResult>
) {

    @JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
    @JsonSubTypes(
        Type(BulkCreateResult::class, name = "create"),
        Type(BulkIndexResult::class, name = "index"),
        Type(BulkUpdateResult::class, name = "update"),
        Type(BulkDeleteResult::class, name = "delete")
    )
    abstract class BulkCommandResult(
        @get:JsonProperty("_index")
        open val index: String,
        @get:JsonProperty("_type")
        open val type: String,
        @get:JsonProperty("_id")
        open val id: String,
        open val status: Long = -1,
        open val result: String? = null,
        open val error: ResultItemError? = null
    )

    data class ResultItemError(val type: String, val reason: String)

    data class BulkCreateResult(
        override val index: String,
        override val type: String,
        override val id: String,
        override val status: Long,
        override val result: String?,
        override val error: ResultItemError?
    ) : BulkCommandResult(index, type, id, status, result, error)

    data class BulkIndexResult(
        override val index: String,
        override val type: String,
        override val id: String,
        override val status: Long,
        override val result: String?,
        override val error: ResultItemError?
    ) : BulkCommandResult(index, type, id, status, result, error)

    data class BulkUpdateResult(
        override val index: String,
        override val type: String,
        override val id: String,
        override val status: Long,
        override val result: String?,
        override val error: ResultItemError?
    ) : BulkCommandResult(index, type, id, status, result, error)

    data class BulkDeleteResult(
        override val index: String,
        override val type: String,
        override val id: String,
        override val status: Long,
        override val result: String?,
        override val error: ResultItemError?
    ) : BulkCommandResult(index, type, id, status, result, error)
}