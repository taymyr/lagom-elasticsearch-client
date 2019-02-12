package org.taymyr.lagom.elasticsearch.document.dsl.update

import com.fasterxml.jackson.annotation.JsonProperty

data class FullScriptedUpdateRequest<Upsert, Params> @JvmOverloads constructor(
    val script: FullScriptedUpdateBody<Params>,
    val upsert: Upsert? = null,
    @JsonProperty("scripted_upsert")
    val scriptedUpsert: Boolean? = null
) : UpdateRequest() {

    class Builder<Upsert, Params> {
        private var script: FullScriptedUpdateBody<Params>? = null
        private var upsert: Upsert? = null
        private var scriptedUpsert: Boolean? = null

        fun script(script: FullScriptedUpdateBody<Params>) = apply { this.script = script }
        fun upsert(upsert: Upsert) = apply { this.upsert = upsert }
        fun scriptedUpsert(scriptedUpsert: Boolean) = apply { this.scriptedUpsert = scriptedUpsert }

        fun build() = FullScriptedUpdateRequest(script ?: error("Field 'script' can not be null"), upsert, scriptedUpsert)
    }

    companion object {
        @JvmStatic fun <Upsert, Params> builder() = Builder<Upsert, Params>()
    }
}