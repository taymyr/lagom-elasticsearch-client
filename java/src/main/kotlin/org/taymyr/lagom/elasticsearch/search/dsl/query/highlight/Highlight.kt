package org.taymyr.lagom.elasticsearch.search.dsl.query.highlight

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.deser.LocaleToStringSerializer
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import java.util.Locale

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/highlighting.html).
 */
enum class BoundaryScanner {
    @JsonProperty("chars")
    CHARS,

    @JsonProperty("sentence")
    SENTENCE,

    @JsonProperty("word")
    WORD
}

enum class Encoder {
    @JsonProperty("html")
    HTML,

    @JsonProperty("default")
    DEFAULT
}

enum class Fragmenter {
    @JsonProperty("simple")
    SIMPLE,

    @JsonProperty("span")
    SPAN
}

enum class TagsSchema {
    @JsonProperty("styled")
    STYLED
}

enum class Type {
    @JsonProperty("unified")
    UNIFIED,

    @JsonProperty("plain")
    PLAIN,

    @JsonProperty("fvh")
    FVH
}

abstract class HighlightItem constructor(
    val boundaryChars: String?,
    val boundaryMaxScan: Int?,
    val boundaryScanner: BoundaryScanner?,
    val boundaryScannerLocale: Locale?,
    val encoder: Encoder?,
    val forceSource: Boolean?,
    val fragmenter: Fragmenter?,
    val fragmentOffset: Int?,
    val fragmentSize: Int?,
    val highlightQuery: Query?,
    val matchedFields: List<String>?,
    val noMatchSize: Int?,
    val numberOfFragments: Int?,
    val order: String?,
    val phraseLimit: Int?,
    val preTags: List<String>?,
    val postTags: List<String>?,
    val requireFieldMatch: Boolean?,
    val maxAnalyzedOffset: Int?,
    val tagsSchema: TagsSchema?,
    val type: Type?
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class Highlight @JvmOverloads constructor(
    boundaryChars: String? = null,
    boundaryMaxScan: Int? = null,
    boundaryScanner: BoundaryScanner? = null,
    @JsonSerialize(using = LocaleToStringSerializer::class)
    boundaryScannerLocale: Locale? = null,
    encoder: Encoder? = null,
    forceSource: Boolean? = null,
    fragmenter: Fragmenter? = null,
    fragmentOffset: Int? = null,
    fragmentSize: Int? = null,
    highlightQuery: Query? = null,
    matchedFields: List<String>? = null,
    noMatchSize: Int? = null,
    numberOfFragments: Int? = null,
    order: String? = null,
    phraseLimit: Int? = null,
    preTags: List<String>? = null,
    postTags: List<String>? = null,
    requireFieldMatch: Boolean? = null,
    maxAnalyzedOffset: Int? = null,
    tagsSchema: TagsSchema? = null,
    type: Type? = null,
    val fields: Map<String, Field>? = null
) : HighlightItem(
    boundaryChars = boundaryChars,
    boundaryMaxScan = boundaryMaxScan,
    boundaryScanner = boundaryScanner,
    boundaryScannerLocale = boundaryScannerLocale,
    encoder = encoder,
    forceSource = forceSource,
    fragmenter = fragmenter,
    fragmentOffset = fragmentOffset,
    fragmentSize = fragmentSize,
    highlightQuery = highlightQuery,
    matchedFields = matchedFields,
    noMatchSize = noMatchSize,
    numberOfFragments = numberOfFragments,
    order = order,
    phraseLimit = phraseLimit,
    preTags = preTags,
    postTags = postTags,
    requireFieldMatch = requireFieldMatch,
    maxAnalyzedOffset = maxAnalyzedOffset,
    tagsSchema = tagsSchema,
    type = type
) {
    class Builder {
        private var boundaryChars: String? = null
        private var boundaryMaxScan: Int? = null
        private var boundaryScanner: BoundaryScanner? = null
        private var boundaryScannerLocale: Locale? = null
        private var encoder: Encoder? = null
        private var forceSource: Boolean? = null
        private var fragmenter: Fragmenter? = null
        private var fragmentOffset: Int? = null
        private var fragmentSize: Int? = null
        private var highlightQuery: Query? = null
        private var matchedFields: List<String>? = null
        private var noMatchSize: Int? = null
        private var numberOfFragments: Int? = null
        private var order: String? = null
        private var phraseLimit: Int? = null
        private var preTags: List<String>? = null
        private var postTags: List<String>? = null
        private var requireFieldMatch: Boolean? = null
        private var maxAnalyzedOffset: Int? = null
        private var tagsSchema: TagsSchema? = null
        private var type: Type? = null
        private var fields: Map<String, Field>? = null

        fun boundaryChars(boundaryChars: String) = apply { this.boundaryChars = boundaryChars }
        fun boundaryMaxScan(boundaryMaxScan: Int) = apply { this.boundaryMaxScan = boundaryMaxScan }
        fun boundaryScanner(boundaryScanner: BoundaryScanner) = apply { this.boundaryScanner = boundaryScanner }
        fun boundaryScannerLocale(boundaryScannerLocale: Locale) = apply { this.boundaryScannerLocale = boundaryScannerLocale }
        fun encoder(encoder: Encoder) = apply { this.encoder = encoder }
        fun forceSource(forceSource: Boolean) = apply { this.forceSource = forceSource }
        fun fragmenter(fragmenter: Fragmenter) = apply { this.fragmenter = fragmenter }
        fun fragmentOffset(fragmentOffset: Int) = apply { this.fragmentOffset = fragmentOffset }
        fun fragmentSize(fragmentSize: Int) = apply { this.fragmentSize = fragmentSize }
        fun highlightQuery(highlightQuery: Query) = apply { this.highlightQuery = highlightQuery }
        fun matchedFields(matchedFields: List<String>) = apply { this.matchedFields = matchedFields }
        fun noMatchSize(noMatchSize: Int) = apply { this.noMatchSize = noMatchSize }
        fun numberOfFragments(numberOfFragments: Int) = apply { this.numberOfFragments = numberOfFragments }
        fun order(order: String) = apply { this.order = order }
        fun phraseLimit(phraseLimit: Int) = apply { this.phraseLimit = phraseLimit }
        fun preTags(preTags: List<String>) = apply { this.preTags = preTags }
        fun postTags(postTags: List<String>) = apply { this.postTags = postTags }
        fun requireFieldMatch(requireFieldMatch: Boolean) = apply { this.requireFieldMatch = requireFieldMatch }
        fun maxAnalyzedOffset(maxAnalyzedOffset: Int) = apply { this.maxAnalyzedOffset = maxAnalyzedOffset }
        fun tagsSchema(tagsSchema: TagsSchema) = apply { this.tagsSchema = tagsSchema }
        fun type(type: Type) = apply { this.type = type }
        fun fields(fields: Map<String, Field>) = apply { this.fields = fields }

        fun build() = Highlight(
            boundaryChars = boundaryChars,
            boundaryMaxScan = boundaryMaxScan,
            boundaryScanner = boundaryScanner,
            boundaryScannerLocale = boundaryScannerLocale,
            encoder = encoder,
            forceSource = forceSource,
            fragmenter = fragmenter,
            fragmentOffset = fragmentOffset,
            fragmentSize = fragmentSize,
            highlightQuery = highlightQuery,
            matchedFields = matchedFields,
            noMatchSize = noMatchSize,
            numberOfFragments = numberOfFragments,
            order = order,
            phraseLimit = phraseLimit,
            preTags = preTags,
            postTags = postTags,
            requireFieldMatch = requireFieldMatch,
            maxAnalyzedOffset = maxAnalyzedOffset,
            tagsSchema = tagsSchema,
            type = type,
            fields = fields
        )
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    class Field @JvmOverloads constructor(
        boundaryChars: String? = null,
        boundaryMaxScan: Int? = null,
        boundaryScanner: BoundaryScanner? = null,
        @JsonSerialize(using = LocaleToStringSerializer::class)
        boundaryScannerLocale: Locale? = null,
        encoder: Encoder? = null,
        forceSource: Boolean? = null,
        fragmenter: Fragmenter? = null,
        fragmentOffset: Int? = null,
        fragmentSize: Int? = null,
        highlightQuery: Query? = null,
        matchedFields: List<String>? = null,
        noMatchSize: Int? = null,
        numberOfFragments: Int? = null,
        order: String? = null,
        phraseLimit: Int? = null,
        preTags: List<String>? = null,
        postTags: List<String>? = null,
        requireFieldMatch: Boolean? = null,
        maxAnalyzedOffset: Int? = null,
        tagsSchema: TagsSchema? = null,
        type: Type? = null
    ) : HighlightItem(
        boundaryChars = boundaryChars,
        boundaryMaxScan = boundaryMaxScan,
        boundaryScanner = boundaryScanner,
        boundaryScannerLocale = boundaryScannerLocale,
        encoder = encoder,
        forceSource = forceSource,
        fragmenter = fragmenter,
        fragmentOffset = fragmentOffset,
        fragmentSize = fragmentSize,
        highlightQuery = highlightQuery,
        matchedFields = matchedFields,
        noMatchSize = noMatchSize,
        numberOfFragments = numberOfFragments,
        order = order,
        phraseLimit = phraseLimit,
        preTags = preTags,
        postTags = postTags,
        requireFieldMatch = requireFieldMatch,
        maxAnalyzedOffset = maxAnalyzedOffset,
        tagsSchema = tagsSchema,
        type = type
    ) {
        class Builder {
            private var boundaryChars: String? = null
            private var boundaryMaxScan: Int? = null
            private var boundaryScanner: BoundaryScanner? = null
            private var boundaryScannerLocale: Locale? = null
            private var encoder: Encoder? = null
            private var forceSource: Boolean? = null
            private var fragmenter: Fragmenter? = null
            private var fragmentOffset: Int? = null
            private var fragmentSize: Int? = null
            private var highlightQuery: Query? = null
            private var matchedFields: List<String>? = null
            private var noMatchSize: Int? = null
            private var numberOfFragments: Int? = null
            private var order: String? = null
            private var phraseLimit: Int? = null
            private var preTags: List<String>? = null
            private var postTags: List<String>? = null
            private var requireFieldMatch: Boolean? = null
            private var maxAnalyzedOffset: Int? = null
            private var tagsSchema: TagsSchema? = null
            private var type: Type? = null

            fun boundaryChars(boundaryChars: String) = apply { this.boundaryChars = boundaryChars }
            fun boundaryMaxScan(boundaryMaxScan: Int) = apply { this.boundaryMaxScan = boundaryMaxScan }
            fun boundaryScanner(boundaryScanner: BoundaryScanner) = apply { this.boundaryScanner = boundaryScanner }
            fun boundaryScannerLocale(boundaryScannerLocale: Locale) = apply { this.boundaryScannerLocale = boundaryScannerLocale }
            fun encoder(encoder: Encoder) = apply { this.encoder = encoder }
            fun forceSource(forceSource: Boolean) = apply { this.forceSource = forceSource }
            fun fragmenter(fragmenter: Fragmenter) = apply { this.fragmenter = fragmenter }
            fun fragmentOffset(fragmentOffset: Int) = apply { this.fragmentOffset = fragmentOffset }
            fun fragmentSize(fragmentSize: Int) = apply { this.fragmentSize = fragmentSize }
            fun highlightQuery(highlightQuery: Query) = apply { this.highlightQuery = highlightQuery }
            fun matchedFields(matchedFields: List<String>) = apply { this.matchedFields = matchedFields }
            fun noMatchSize(noMatchSize: Int) = apply { this.noMatchSize = noMatchSize }
            fun numberOfFragments(numberOfFragments: Int) = apply { this.numberOfFragments = numberOfFragments }
            fun order(order: String) = apply { this.order = order }
            fun phraseLimit(phraseLimit: Int) = apply { this.phraseLimit = phraseLimit }
            fun preTags(preTags: List<String>) = apply { this.preTags = preTags }
            fun postTags(postTags: List<String>) = apply { this.postTags = postTags }
            fun requireFieldMatch(requireFieldMatch: Boolean) = apply { this.requireFieldMatch = requireFieldMatch }
            fun maxAnalyzedOffset(maxAnalyzedOffset: Int) = apply { this.maxAnalyzedOffset = maxAnalyzedOffset }
            fun tagsSchema(tagsSchema: TagsSchema) = apply { this.tagsSchema = tagsSchema }
            fun type(type: Type) = apply { this.type = type }

            fun build() = Field(
                boundaryChars = boundaryChars,
                boundaryMaxScan = boundaryMaxScan,
                boundaryScanner = boundaryScanner,
                boundaryScannerLocale = boundaryScannerLocale,
                encoder = encoder,
                forceSource = forceSource,
                fragmenter = fragmenter,
                fragmentOffset = fragmentOffset,
                fragmentSize = fragmentSize,
                highlightQuery = highlightQuery,
                matchedFields = matchedFields,
                noMatchSize = noMatchSize,
                numberOfFragments = numberOfFragments,
                order = order,
                phraseLimit = phraseLimit,
                preTags = preTags,
                postTags = postTags,
                requireFieldMatch = requireFieldMatch,
                maxAnalyzedOffset = maxAnalyzedOffset,
                tagsSchema = tagsSchema,
                type = type
            )
        }

        companion object {
            @JvmStatic
            fun builder() = Builder()
        }
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
