package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
data class CreateIndexResult(val acknowledged: Boolean, val shardsAcknowledged: Boolean, val index: String)