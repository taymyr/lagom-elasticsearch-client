package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonValue

/**
 * See [[https://www.elastic.co/guide/en/elasticsearch/reference/current/search-fields.html#source-filtering]]
 */
sealed class SourceFilter<T>(open val value: T) {
    data class Inclusion(@get:JsonValue override val value: Boolean) : SourceFilter<Boolean>(value)

    data class SinglePath(@get:JsonValue override val value: String) : SourceFilter<String>(value)

    data class MultiPath(@get:JsonValue override val value: List<String>) : SourceFilter<List<String>>(value)

    data class IncExc(val includes: List<String>, val excludes: List<String>)

    data class IncludeExclude(@get:JsonValue override val value: IncExc) : SourceFilter<IncExc>(value)

    companion object {
        @JvmField
        val EXCLUDE_SOURCE = Inclusion(false)
        @JvmField
        val INCLUDE_SOURCE = Inclusion(true)
        @JvmStatic
        fun singlePath(value: String) = SinglePath(value)
        @JvmStatic
        fun multiPath(value: List<String>) = MultiPath(value)
        @JvmStatic
        fun multiPath(includes: List<String>, excludes: List<String>) = IncludeExclude(IncExc(includes, excludes))
    }
}
