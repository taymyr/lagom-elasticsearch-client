package org.taymyr.lagom.elasticsearch.dsl.search

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
data class CompoundQuery @JsonCreator constructor(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val bool: BoolQuery?,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("dis_max")
    val disMax: DisMaxQuery?
) : Query {

    class Builder {

        private var bool: BoolQuery? = null
        private var disMax: DisMaxQuery? = null

        fun bool(bool: BoolQuery?) = apply { this.bool = bool }
        fun disMax(disMax: DisMaxQuery?) = apply { this.disMax = disMax }

        fun build() = CompoundQuery(bool, disMax)
    }
}
