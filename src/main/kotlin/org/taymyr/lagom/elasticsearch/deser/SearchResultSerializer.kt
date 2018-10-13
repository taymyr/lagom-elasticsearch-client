package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedDeserializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import org.pcollections.PSequence
import org.pcollections.TreePVector
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult
import java.util.Optional

class SearchResultSerializer(private val mapper: ObjectMapper, private val clazz: Class<*>) : StrictMessageSerializer<SearchResult<*>> {

    private val defaultProtocol = MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty())

    internal inner class Deserializer : NegotiatedDeserializer<SearchResult<*>, ByteString> {
        override fun deserialize(wire: ByteString): SearchResult<*> {
            val inputStream = wire.iterator().asInputStream()
            val jsonNode = mapper.readTree(inputStream)
            val reader = mapper.reader().forType(clazz)
            val result: SearchResult<*> = reader.readValue(jsonNode)
            result.initialize(jsonNode, reader)
            return result
        }
    }

    override fun acceptResponseProtocols(): PSequence<MessageProtocol> = TreePVector.from(listOf(defaultProtocol))

    override fun serializerForResponse(acceptedMessageProtocols: MutableList<MessageProtocol>):
        MessageSerializer.NegotiatedSerializer<SearchResult<*>, ByteString> = throw NotImplementedError("Unsupported now")

    override fun serializerForRequest():
        MessageSerializer.NegotiatedSerializer<SearchResult<*>, ByteString> = throw NotImplementedError("Unsupported now")

    override fun deserializer(protocol: MessageProtocol):
        MessageSerializer.NegotiatedDeserializer<SearchResult<*>, ByteString> = Deserializer()
}