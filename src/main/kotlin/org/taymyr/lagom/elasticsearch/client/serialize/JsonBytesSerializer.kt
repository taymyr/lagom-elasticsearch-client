package org.taymyr.lagom.elasticsearch.client.serialize

import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import com.lightbend.lagom.javadsl.api.transport.NotAcceptable
import com.lightbend.lagom.javadsl.api.transport.UnsupportedMediaType
import org.taymyr.lagom.javadsl.api.transport.MessageProtocols

class JsonBytesSerializer : StrictMessageSerializer<JsonBytes> {

    override fun serializerForResponse(acceptedMessageProtocols: MutableList<MessageProtocol>?):
            MessageSerializer.NegotiatedSerializer<JsonBytes, ByteString> {
        return if(acceptedMessageProtocols?.isEmpty() != false) {
            serializerForRequest()
        } else {
            acceptedMessageProtocols.firstOrNull { p ->
                p.contentType().orElse("application/json") == "application/json"
            }?.let {
                Serializer(it.withContentType("application/json"))
            } ?: throw NotAcceptable(acceptedMessageProtocols, MessageProtocols.JSON_UTF_8)
        }
    }

    override fun serializerForRequest() = Serializer(MessageProtocols.JSON_UTF_8)

    override fun deserializer(protocol: MessageProtocol):
            MessageSerializer.NegotiatedDeserializer<JsonBytes, ByteString> {
        return if("application/json" == protocol.contentType().orElse("application/json"))
            Deserializer(protocol.charset().orElse("utf-8"))
        else
            throw UnsupportedMediaType(protocol, MessageProtocols.JSON_UTF_8)
    }

    class Serializer(
            private val protocol: MessageProtocol
    ) : MessageSerializer.NegotiatedSerializer<JsonBytes, ByteString> {

        override fun protocol() = protocol

        override fun serialize(messageEntity: JsonBytes): ByteString {
            println(messageEntity)
            return ByteString.fromArray(messageEntity.bytes)!!
        }

    }

    class Deserializer(
            private val charset: String
    ) : MessageSerializer.NegotiatedDeserializer<JsonBytes, ByteString> {

        override fun deserialize(wire: ByteString) = JsonBytes(wire.decodeString(charset).toByteArray())

    }

}
