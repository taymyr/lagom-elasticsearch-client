package org.taymyr.lagom.elasticsearch.document.dsl.update

import com.fasterxml.jackson.annotation.JsonProperty

data class DocUpdateRequest<Document> @JvmOverloads constructor(
    val doc: Document,
    @JsonProperty("doc_as_upsert")
    val docAsUpsert: Boolean? = null,
    @JsonProperty("detect_noop")
    val detectNoOp: Boolean? = null
) : UpdateRequest() {

    class Builder<Document> {
        private var doc: Document? = null
        private var docAsUpsert: Boolean? = null
        private var detectNoOp: Boolean? = null

        fun doc(doc: Document) = apply { this.doc = doc }
        fun docAsUpsert(docAsUpsert: Boolean) = apply { this.docAsUpsert = docAsUpsert }
        fun detectNoOp(detectNoOp: Boolean) = apply { this.detectNoOp = detectNoOp }

        fun build() = DocUpdateRequest(doc ?: error("Field 'doc' can not be null"), docAsUpsert, detectNoOp)
    }

    companion object {
        @JvmStatic
        fun <Document> builder() = DocUpdateRequest.Builder<Document>()
    }
}
