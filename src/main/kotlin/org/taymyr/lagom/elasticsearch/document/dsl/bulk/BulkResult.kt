package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter

/**
 * @author Ilya Korshunov
 */
data class BulkResult @JsonCreator constructor(
    @JsonProperty("errors")
    val errors: Boolean
) {

    lateinit var items: List<ResultItem>

    @Suppress("unused")
    @JsonSetter("items")
    fun itemsSetter(value: List<Map<String, ResultItem>>) {
        this.items = value.map { i ->
            i.map { v -> v.value.apply { command = v.key } }[0]
        }
    }
    data class ResultItem(
        val index: String,
        val type: String,
        val id: String,
        val status: Long,
        val result: String?,
        val error: ResultItemError?,
        var command: String = ""
    ) {
        @JsonCreator constructor(
            @JsonProperty("_index") index: String,
            @JsonProperty("_type") type: String,
            @JsonProperty("_id") id: String,
            @JsonProperty("status") status: Long,
            @JsonProperty("result") result: String?,
            @JsonProperty("error") error: ResultItemError?
        ) : this(index, type, id, status, result, error, "")
    }
    data class ResultItemError @JsonCreator constructor(
        @JsonProperty("type")
        val type: String,
        @JsonProperty("reason")
        val reason: String
    )
}