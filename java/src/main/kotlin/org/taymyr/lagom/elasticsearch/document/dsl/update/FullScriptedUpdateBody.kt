package org.taymyr.lagom.elasticsearch.document.dsl.update

data class FullScriptedUpdateBody<Params>(
    val id: String?,
    val source: String,
    val lang: String?,
    val params: Params?
) {
    class Builder<Params> {
        private var id: String? = null
        private var source: String? = null
        private var lang: String? = null
        private var params: Params? = null

        fun id(id: String) = apply { this.id = id }
        fun source(source: String) = apply { this.source = source }
        fun lang(lang: String) = apply { this.lang = lang }
        fun params(params: Params) = apply { this.params = params }
        fun build() = FullScriptedUpdateBody(id, source ?: error("Field 'source' can not be null"), lang, params)
    }

    companion object {
        @JvmStatic fun <Params> updateScript() = Builder<Params>()
    }
}