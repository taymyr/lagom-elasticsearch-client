package org.taymyr.lagom.elasticsearch.deser

import com.lightbend.lagom.javadsl.api.deser.PathParamSerializer
import org.pcollections.PSequence
import org.pcollections.TreePVector

/**
 * Serializer [List] of [String] for dynamic parts of path.
 * The comma is elements separator.
 * @author Sergey Morgunov
 */
object ListStringPathParamSerializer : PathParamSerializer<List<String>> {

    override fun serialize(parameter: List<String>): PSequence<String> {
        return TreePVector.singleton(parameter.joinToString(","))
    }

    override fun deserialize(parameters: PSequence<String>): List<String> {
        throw NotImplementedError("Deserialize temporary not supported")
    }
}