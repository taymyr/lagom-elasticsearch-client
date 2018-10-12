package org.taymyr.lagom.elasticsearch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.document.dsl.Document
import org.taymyr.lagom.elasticsearch.search.dsl.Hits
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Term

abstract class AbstractSampleDocument {
    abstract val user: String
    abstract val message: String?
    abstract val age: Int?
    abstract val balance: Double?
}

data class SampleDocument(
    override val user: String,
    override val message: String? = null,
    override val age: Int? = null,
    override val balance: Double? = null
) : AbstractSampleDocument()

@JsonInclude(ALWAYS)
data class SampleDocumentWithForcedNulls(
    override val user: String,
    override val message: String? = null,
    override val age: Int? = null,
    override val balance: Double? = null
) : AbstractSampleDocument()

data class IndexedSampleDocument(override val source: SampleDocument) : Document<SampleDocument>()

data class SampleDocumentResult(override val hits: Hits<SampleDocument>) : SearchResult<SampleDocument>()

data class UserKeywordTerm(@get:JsonProperty("user.keyword") val user: String) : Term
data class MessageKeywordTerm(@get:JsonProperty("message.keyword") val message: String) : Term