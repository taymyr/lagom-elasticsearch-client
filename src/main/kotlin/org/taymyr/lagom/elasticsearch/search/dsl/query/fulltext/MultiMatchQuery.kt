package org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

data class MultiMatchQuery(
    @JsonProperty("multi_match")
    val multiMatch: MultiMatch
) : Query {

    companion object {

        @JvmStatic fun ofMultiMatch(multiMatch: MultiMatch) = MultiMatchQuery(multiMatch)
        @JvmStatic fun multiMatchSimpleBuilder() = MultiMatchSimple.MultiMatchSimpleBuilder()
    }
}