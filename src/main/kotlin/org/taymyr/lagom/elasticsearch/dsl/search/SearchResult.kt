package org.taymyr.lagom.elasticsearch.dsl.search

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.DTOAnnotation
import org.taymyr.lagom.elasticsearch.Indexable
import java.util.stream.Stream

@DTOAnnotation
@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchResult<T : Indexable> @JsonCreator constructor(
    @JsonProperty("hits") val hits: Hits<T>
) {

    fun getIndexedItems(): Stream<T> = hits.hits.stream().map { h -> h.item }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchResult<*>

        if (hits != other.hits) return false

        return true
    }

    override fun hashCode(): Int {
        return hits.hashCode()
    }
}
