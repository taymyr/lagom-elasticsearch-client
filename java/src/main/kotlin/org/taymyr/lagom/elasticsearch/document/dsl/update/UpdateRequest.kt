package org.taymyr.lagom.elasticsearch.document.dsl.update

abstract class UpdateRequest {
    companion object {
        @JvmStatic fun <Document> docUpdate() = DocUpdateRequest.Builder<Document>()
        @JvmStatic fun scriptUpdate(script: String) = ShortScriptedUpdateRequest(script)
        @JvmStatic fun <Upsert, Params> scriptUpdate() = FullScriptedUpdateRequest.Builder<Upsert, Params>()
    }
}