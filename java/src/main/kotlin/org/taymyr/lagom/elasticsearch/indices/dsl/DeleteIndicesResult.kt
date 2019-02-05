package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
data class DeleteIndicesResult(val acknowledged: Boolean)