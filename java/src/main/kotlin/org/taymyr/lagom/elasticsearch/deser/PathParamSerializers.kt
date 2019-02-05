package org.taymyr.lagom.elasticsearch.deser

import com.lightbend.lagom.javadsl.api.deser.PathParamSerializer
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers

/**
 * Serializer [List] of [String] for dynamic parts of path.
 * The comma is elements separator.
 */
val LIST: PathParamSerializer<List<String>> = PathParamSerializers.required<List<String>>(
    "List(String)",
    { i -> i.split(",") },
    { i -> i.joinToString(",") }
)