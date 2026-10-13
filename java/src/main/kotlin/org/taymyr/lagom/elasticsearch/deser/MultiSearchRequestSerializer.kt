package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import akka.util.ByteStringBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import org.taymyr.lagom.elasticsearch.search.dsl.msearch.MultiSearchRequest
import java.io.OutputStream
import java.util.Optional

/**
 * Serializer for [MultiSearchRequest].
 */
internal class MultiSearchRequestSerializer(val mapper: ObjectMapper) : StrictMessageSerializer<MultiSearchRequest> {

    private val defaultProtocol = MessageProtocol(Optional.of("application/x-ndjson"), Optional.empty(), Optional.empty())

    inner class Serializer(private val protocol: MessageProtocol) : MessageSerializer.NegotiatedSerializer<MultiSearchRequest, ByteString> {
        override fun protocol() = protocol
        override fun serialize(messageEntity: MultiSearchRequest): ByteString {
            val builder = ByteStringBuilder()
            val os = builder.asOutputStream()
            messageEntity.searches.forEach { search ->
                mapper.writeValue(os, search.header)
                os.writeNextLine()
                mapper.writeValue(os, search.body)
                os.writeNextLine()
            }
            return builder.result()
        }

        private fun OutputStream.writeNextLine(): OutputStream = this.also { it.write('\n'.code) }
    }

    override fun serializerForResponse(acceptedMessageProtocols: List<MessageProtocol>): MessageSerializer.NegotiatedSerializer<MultiSearchRequest, ByteString> =
        serializerForRequest()

    override fun serializerForRequest(): MessageSerializer.NegotiatedSerializer<MultiSearchRequest, ByteString> = Serializer(defaultProtocol)

    override fun deserializer(protocol: MessageProtocol?): MessageSerializer.NegotiatedDeserializer<MultiSearchRequest, ByteString> =
        throw NotImplementedError("Unsupported now")
}
