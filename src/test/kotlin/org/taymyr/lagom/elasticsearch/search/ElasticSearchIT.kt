package org.taymyr.lagom.elasticsearch.search

import io.kotlintest.extensions.TestListener
import io.kotlintest.specs.WordSpec
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic

class ElasticSearchIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        // TODO
    }

    companion object : LagomClientAndEmbeddedElastic()
}