package org.taymyr.lagom.elasticsearch.document.dsl.update

import com.fasterxml.jackson.annotation.JsonProperty

data class FullScriptedUpdateRequest<Upsert, Params>(
    @JsonProperty("scripted_upsert")
    val scriptedUpsert: Boolean?,
    val script: FullScriptedUpdateBody<Params>,
    val upsert: Upsert?
) : UpdateRequest() {
    class Builder<Upsert, Params> {
        private var scriptedUpsert: Boolean? = null
        private var script: FullScriptedUpdateBody<Params>? = null
        private var upsert: Upsert? = null

        fun scriptedUpsert(scriptedUpsert: Boolean) = apply { this.scriptedUpsert = scriptedUpsert }
        fun script(script: FullScriptedUpdateBody<Params>) = apply { this.script = script }
        fun upsert(upsert: Upsert) = apply { this.upsert = upsert }
        fun build() = FullScriptedUpdateRequest(scriptedUpsert, script ?: error("Field 'script' can not be null"), upsert)
    }
}