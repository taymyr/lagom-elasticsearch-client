package org.taymyr.lagom.elasticsearch.dsl.search

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
data class QueryRoot(
    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    val query: Query?,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("from")
    val pageNumber: Int?,

    @get:JsonInclude(JsonInclude.Include.NON_NULL)
    @get:JsonProperty("size")
    val pageSize: Int?,

    @get:JsonInclude(JsonInclude.Include.NON_EMPTY)
    @get:JsonProperty("sort")
    val sort: List<SortField>?
) {

    class Builder {

        private var query: Query? = null
        private var pageNumber: Int? = null
        private var pageSize: Int? = null
        private var sort: List<SortField>? = null

        fun query(query: Query?) = apply { this.query = query }
        fun pageNumber(pageNumber: Int?) = apply { this.pageNumber = pageNumber }
        fun pageSize(pageSize: Int?) = apply { this.pageSize = pageSize }

        fun sort(sort: List<SortField>?) = apply { this.sort = sort }
        fun sort(vararg sort: SortField) = sort(sort.asList())

        fun build() = QueryRoot(
            query,
            pageNumber,
            pageSize,
            sort
        )
    }
}
