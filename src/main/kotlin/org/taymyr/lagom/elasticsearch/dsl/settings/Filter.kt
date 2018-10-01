package org.taymyr.lagom.elasticsearch.dsl.settings

import org.taymyr.lagom.elasticsearch.DTOAnnotation

@DTOAnnotation
abstract class Filter(
    val type: String
)
