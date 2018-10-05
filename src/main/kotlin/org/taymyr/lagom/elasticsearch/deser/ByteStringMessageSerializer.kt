package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.deser.DeserializationException
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedDeserializer
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedSerializer
import com.lightbend.lagom.javadsl.api.deser.SerializationException
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import com.lightbend.lagom.javadsl.api.transport.NotAcceptable
import com.lightbend.lagom.javadsl.api.transport.UnsupportedMediaType
import java.util.Optional.empty
import java.util.Optional.of

/**
 * NOP message serializer for [ByteString].
 *
 * @author Sergey Morgunov
 */
class ByteStringMessageSerializer : StrictMessageSerializer<ByteString> {

    private val defaultProtocol = MessageProtocol(of("application/json"), of("utf-8"), empty())

    internal inner class ByteStringSerializer(private val protocol: MessageProtocol) : NegotiatedSerializer<ByteString, ByteString> {
        override fun protocol(): MessageProtocol = protocol

        @Throws(SerializationException::class)
        override fun serialize(s: ByteString): ByteString = s
    }

    internal inner class ByteStringDeserializer : NegotiatedDeserializer<ByteString, ByteString> {
        @Throws(DeserializationException::class)
        override fun deserialize(wire: ByteString): ByteString = wire
    }

    override fun serializerForRequest(): NegotiatedSerializer<ByteString, ByteString> {
        return ByteStringSerializer(defaultProtocol)
    }

    @Throws(UnsupportedMediaType::class)
    override fun deserializer(protocol: MessageProtocol): NegotiatedDeserializer<ByteString, ByteString> {
        return ByteStringDeserializer()
    }

    @Throws(NotAcceptable::class)
    override fun serializerForResponse(acceptedMessageProtocols: List<MessageProtocol>): NegotiatedSerializer<ByteString, ByteString> {
        return serializerForRequest()
    }
}