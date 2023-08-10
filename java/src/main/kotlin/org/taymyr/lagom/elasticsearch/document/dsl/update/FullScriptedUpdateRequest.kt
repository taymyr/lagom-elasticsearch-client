package org.taymyr.lagom.elasticsearch.document.dsl.update

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.script.Script

data class FullScriptedUpdateRequest<Upsert> @JvmOverloads constructor(
    val script: Script,
    val upsert: Upsert? = null,
    @JsonProperty("scripted_upsert")
    val scriptedUpsert: Boolean? = null
) : UpdateRequest() {

    class Builder<Upsert> {
        private var script: Script? = null
        private var upsert: Upsert? = null
        private var scriptedUpsert: Boolean? = null

        fun script(script: Script) = apply { this.script = script }
        fun upsert(upsert: Upsert) = apply { this.upsert = upsert }
        fun scriptedUpsert(scriptedUpsert: Boolean) = apply { this.scriptedUpsert = scriptedUpsert }

        fun build() = FullScriptedUpdateRequest(script ?: error("Field 'script' can not be null"), upsert, scriptedUpsert)
    }

    companion object {
        @JvmStatic
        fun <Upsert> builder() = Builder<Upsert>()
    }
}
