package org.taymyr.lagom.elasticsearch.document.dsl.update

data class FullScriptedUpdateBody<Params> @JvmOverloads constructor(
    val source: String,
    val id: String? = null,
    val lang: String? = null,
    val params: Params? = null
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

        fun build() = FullScriptedUpdateBody(source ?: error("Field 'source' can not be null"), id, lang, params)
    }

    companion object {
        @JvmStatic
        fun <Params> builder() = Builder<Params>()
    }
}