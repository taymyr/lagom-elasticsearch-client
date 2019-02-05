package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import akka.util.ByteStringBuilder
import com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS
import com.fasterxml.jackson.databind.ObjectMapper
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedDeserializer
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedSerializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkIndex
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkUpdate
import java.io.OutputStream
import java.util.Optional.empty
import java.util.Optional.of

class BulkRequestSerializer(val mapper: ObjectMapper) : StrictMessageSerializer<BulkRequest> {

    private val defaultProtocol = MessageProtocol(of("application/json"), of("utf-8"), empty())
    private val mapperAlwaysInclusion = mapper.copy().setSerializationInclusion(ALWAYS)

    inner class Serializer(private val protocol: MessageProtocol) : NegotiatedSerializer<BulkRequest, ByteString> {
        override fun protocol() = protocol
        override fun serialize(entity: BulkRequest): ByteString {
            val builder = ByteStringBuilder()
            val os = builder.asOutputStream()
            entity.commands.forEach { command ->
                mapper.writeValue(os, command)
                when (command) {
                    is BulkCreate -> mapper.writeValue(os.writeNextLine(), command.element.source)
                    is BulkIndex -> mapper.writeValue(os.writeNextLine(), command.element.source)
                    is BulkUpdate -> mapperAlwaysInclusion.writeValue(os.writeNextLine(), mapOf("doc" to command.element.source))
                    else -> {} // Ignore
                }
                os.writeNextLine()
            }
            return builder.result()
        }
        private fun OutputStream.writeNextLine(): OutputStream = this.also { it.write('\n'.toInt()) }
    }

    override fun serializerForResponse(acceptedMessageProtocols: MutableList<MessageProtocol>) = serializerForRequest()

    override fun serializerForRequest(): NegotiatedSerializer<BulkRequest, ByteString> = Serializer(defaultProtocol)

    override fun deserializer(protocol: MessageProtocol?): NegotiatedDeserializer<BulkRequest, ByteString> =
        throw NotImplementedError("Unsupported now")
}
