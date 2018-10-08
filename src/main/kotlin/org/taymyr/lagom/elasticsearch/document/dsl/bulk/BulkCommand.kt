package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * @author Ilya Korshunov
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
abstract class BulkCommand(
    @get:JsonProperty("_id")
    open val id: String
)
