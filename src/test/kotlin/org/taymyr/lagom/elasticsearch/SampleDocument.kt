package org.taymyr.lagom.elasticsearch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS
import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.document.dsl.Document
import org.taymyr.lagom.elasticsearch.search.dsl.Hits
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrase
import org.taymyr.lagom.elasticsearch.search.dsl.query.fulltext.MatchPhrasePrefix
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Prefix
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Regexp
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Term
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.Wildcard
import java.time.LocalDateTime

abstract class AbstractSampleDocument {
    abstract val user: String
    abstract val message: String?
    abstract val age: Int?
    abstract val balance: Double?
    abstract val creationDate: LocalDateTime?
}

data class SampleDocument(
    override val user: String,
    override val message: String? = null,
    override val age: Int? = null,
    override val balance: Double? = null,
    override val creationDate: LocalDateTime? = null
) : AbstractSampleDocument()

@JsonInclude(ALWAYS)
data class SampleDocumentWithForcedNulls(
    override val user: String,
    override val message: String? = null,
    override val age: Int? = null,
    override val balance: Double? = null,
    override val creationDate: LocalDateTime? = null
) : AbstractSampleDocument()

data class IndexedSampleDocument(override val source: SampleDocument) : Document<SampleDocument>()

data class SampleDocumentResult(override val hits: Hits<SampleDocument>) : SearchResult<SampleDocument>()

data class UserKeywordTerm(@get:JsonProperty("user.keyword") val user: String) : Term
data class MessageKeywordTerm(@get:JsonProperty("message.keyword") val message: String) : Term
// Full text searching test:
data class UserMatchPhrase(val user: String) : MatchPhrase
data class UserMatchPhrasePrefix(val user: String) : MatchPhrasePrefix
data class UserPrefix(val user: String) : Prefix
data class MessageMatchPhrase(val message: String) : MatchPhrase
data class MessageMatchPhrasePrefix(val message: String) : MatchPhrasePrefix
data class MessagePrefix(val message: String) : Prefix
data class MessageWildcard(val message: String) : Wildcard
data class MessageRegexp(val message: String) : Regexp
