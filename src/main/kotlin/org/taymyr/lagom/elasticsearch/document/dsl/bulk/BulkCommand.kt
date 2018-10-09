package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@JsonSubTypes(
    Type(BulkCreate::class, name = "create"),
    Type(BulkIndex::class, name = "index"),
    Type(BulkUpdate::class, name = "update"),
    Type(BulkDelete::class, name = "delete")
)
abstract class BulkCommand(
    @get:JsonProperty("_id")
    open val id: String
)
