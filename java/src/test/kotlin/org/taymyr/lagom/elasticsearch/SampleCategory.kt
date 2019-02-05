package org.taymyr.lagom.elasticsearch

import org.taymyr.lagom.elasticsearch.document.dsl.Document
import org.taymyr.lagom.elasticsearch.search.dsl.Hits
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult

data class SampleCategory(
    val id: Long?,
    val name: List<String>?,
    val title: Map<String, String>?,
    val technicalName: String?,
    val attachAllowed: Boolean?,
    val fullText: String?,
    val fullTextBoosted: String?
)

data class IndexedSampleCategory(override val source: SampleCategory) : Document<SampleCategory>()

data class SampleCategoryResult(override val hits: Hits<SampleCategory>) : SearchResult<SampleCategory>()
