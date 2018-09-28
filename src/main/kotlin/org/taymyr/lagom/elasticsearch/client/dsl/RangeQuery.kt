package org.taymyr.lagom.elasticsearch.client.dsl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import org.taymyr.lagom.elasticsearch.client.DTOAnnotation
import java.util.*

@DTOAnnotation
data class RangeQuery(
        val range: Range
) : Query {

    interface Range

    companion object {

        @JvmStatic fun of(range: Range) = RangeQuery(range)

    }

    @DTOAnnotation
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    data class LteGte @JsonCreator constructor(
            val lte: OptionalInt,
            val gte: OptionalInt
    )

}
