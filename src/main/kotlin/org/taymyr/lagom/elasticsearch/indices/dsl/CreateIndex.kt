package org.taymyr.lagom.elasticsearch.indices.dsl

import javax.annotation.concurrent.Immutable

@Immutable
class CreateIndex(val settings: Settings? = null) {

    @Immutable
    data class Settings(val index: Index? = null)

    @Immutable
    data class Index(
        val numberOfShards: Int?,
        val numberOfReplicas: Int?
    )
}
