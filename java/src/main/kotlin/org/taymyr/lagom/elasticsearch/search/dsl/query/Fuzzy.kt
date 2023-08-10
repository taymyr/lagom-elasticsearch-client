package org.taymyr.lagom.elasticsearch.search.dsl.query

import com.fasterxml.jackson.annotation.JsonProperty

data class Fuzzy(
    val fuzziness: String,
    val transpositions: Boolean? = null,
    @JsonProperty("min_length")
    val minLength: Int? = null,
    @JsonProperty("prefix_length")
    val prefixLength: Int? = null,
    @JsonProperty("unicode_aware")
    val unicodeAware: Boolean? = null
) {

    class Builder {

        private var fuzziness: String? = null
        private var transpositions: Boolean? = null
        private var minLength: Int? = null
        private var prefixLength: Int? = null
        private var unicodeAware: Boolean? = null

        fun fuzziness(fuzziness: Int) = apply { this.fuzziness = fuzziness.toString() }
        fun transpositions(transpositions: Boolean) = apply { this.transpositions = transpositions }
        fun minLength(minLength: Int) = apply { this.minLength = minLength }
        fun prefixLength(prefixLength: Int) = apply { this.prefixLength = prefixLength }
        fun unicodeAware(unicodeAware: Boolean) = apply { this.unicodeAware = unicodeAware }

        fun build() = Fuzzy(
            fuzziness ?: "AUTO",
            transpositions,
            minLength,
            prefixLength,
            unicodeAware
        )
    }

    companion object {
        @JvmField
        val AUTO: Fuzzy = Fuzzy("AUTO")

        @JvmStatic
        fun builder() = Fuzzy.Builder()
    }
}
