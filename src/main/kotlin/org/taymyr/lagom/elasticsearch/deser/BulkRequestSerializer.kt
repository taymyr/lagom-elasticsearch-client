package org.taymyr.lagom.elasticsearch.deser

import akka.util.ByteString
import akka.util.ByteStringBuilder
import com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkIndex
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkUpdate
import java.io.OutputStream
import java.util.Optional

class BulkRequestSerializer : StrictMessageSerializer<BulkRequest> {
    private val defaultProtocol = MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty())
    internal inner class Serializer(
        private val protocol: MessageProtocol
    ) : MessageSerializer.NegotiatedSerializer<BulkRequest, ByteString> {
        private val mapper = ElasticSerializerFactory().objectMapper
        private val mapperAlwaysNull = mapper.copy().setSerializationInclusion(ALWAYS)
        override fun protocol() = protocol
        override fun serialize(entity: BulkRequest): ByteString {
            val builder = ByteStringBuilder()
            val os = builder.asOutputStream()
            entity.commands.forEach { command ->
                run {
                    mapper.writeValue(os, command)
                    when (command) {
                        is BulkCreate -> run {
                            os.writeNextLine()
                            mapper.writeValue(os, command.element.source)
                        }
                        is BulkIndex -> run {
                            os.writeNextLine()
                            mapper.writeValue(os, command.element.source)
                        }
                        is BulkUpdate -> run {
                            os.writeNextLine()
                            val valueToSerialize = mapOf("doc" to command.element.source)
                            mapperAlwaysNull.writeValue(os, valueToSerialize)
                        }
                        else -> {} // Ignore
                    }
                    os.writeNextLine()
                }
            }
            return builder.result()
        }
        private fun OutputStream.writeNextLine() = this.write('\n'.toInt())
    }
    override fun serializerForResponse(acceptedMessageProtocols: MutableList<MessageProtocol>) = serializerForRequest()
    override fun serializerForRequest(): MessageSerializer.NegotiatedSerializer<BulkRequest, ByteString> = Serializer(defaultProtocol)
    override fun deserializer(protocol: MessageProtocol?):
        MessageSerializer.NegotiatedDeserializer<BulkRequest, ByteString> = throw NotImplementedError("Unsupported now")
}
