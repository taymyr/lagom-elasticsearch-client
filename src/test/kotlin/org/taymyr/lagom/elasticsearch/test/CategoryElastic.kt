package org.taymyr.lagom.elasticsearch.test

import org.taymyr.lagom.elasticsearch.DTOAnnotation
import org.taymyr.lagom.elasticsearch.Indexable

@DTOAnnotation
data class CategoryElastic(
    val id: Long,
    val name: List<String>,
    val title: Map<String, String>,
    val technicalName: String,
    val attachAllowed: Boolean,
    val parentId: Long
) : Indexable {

    override fun getId() = id.toString()
}
