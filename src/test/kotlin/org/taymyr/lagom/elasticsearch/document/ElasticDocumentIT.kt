package org.taymyr.lagom.elasticsearch.document

import io.kotlintest.extensions.TestListener
import io.kotlintest.specs.WordSpec
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic

class ElasticDocumentIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        // TODO
    }

    companion object : LagomClientAndEmbeddedElastic()
}