package org.taymyr.lagom.elasticsearch.search.dsl.query

data class Fuzzy(val fuzziness: String) {

    companion object {
        @JvmField
        val AUTO: Fuzzy = Fuzzy("AUTO")
    }
}