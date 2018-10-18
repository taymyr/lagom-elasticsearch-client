package org.taymyr.lagom.elasticsearch.search.dsl.query

data class Fuzzy(val fuzziness: String) {

    companion object {

        @JvmStatic fun auto(): Fuzzy {
            return Fuzzy("AUTO")
        }
    }
}