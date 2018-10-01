package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
data class IndexInfo(val settings: Settings) {

    @Immutable
    data class Settings(val index: Index)

    @Immutable
    data class Index(
        val numberOfShards: Int,
        val numberOfReplicas: Int
    )
}