package org.taymyr.lagom.elasticsearch

import org.taymyr.lagom.elasticsearch.document.dsl.Document
import org.taymyr.lagom.elasticsearch.search.dsl.Hits
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult

data class SampleDocument(val user: String, val message: String?)

data class IndexedSampleDocument(override val source: SampleDocument) : Document<SampleDocument>()

data class SampleDocumentResult(override val hits: Hits<SampleDocument>) : SearchResult<SampleDocument>()