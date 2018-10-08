package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import akka.util.ByteString
import akka.util.ByteStringBuilder
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer
import com.lightbend.lagom.javadsl.api.deser.StrictMessageSerializer
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import java.util.Optional

/**
 * @author Ilya Korshunov
 */
class BulkRequestSerializer : StrictMessageSerializer<BulkRequest> {
    private val defaultProtocol = MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty())
    internal inner class Serializer(
        private val protocol: MessageProtocol
    ) : MessageSerializer.NegotiatedSerializer<BulkRequest, ByteString> {
        private val mapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(Jdk8Module())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        override fun protocol() = protocol
        override fun serialize(entity: BulkRequest): ByteString {
            val builder = ByteStringBuilder()
            val os = builder.asOutputStream()
            entity.commands.forEach { command ->
                run {
                    mapper.writeValue(os, command)
                    when (command) {
                        is CreateBulkCommand -> run {
                            builder.addEmptyLine()
                            mapper.writeValue(os, command.element.source)
                        }
                        is IndexBulkCommand -> run {
                            builder.addEmptyLine()
                            mapper.writeValue(os, command.element.source)
                        }
                        is UpdateBulkCommand -> run {
                            builder.addEmptyLine()
                            val valueToSerialize = mapOf("doc" to command.element.source)
                            if (command.ignoreNull) {
                                mapper.writeValue(os, valueToSerialize)
                            } else {
                                mapper.copy().setSerializationInclusion(JsonInclude.Include.ALWAYS)
                                    .writeValue(os, valueToSerialize)
                            }
                        }
                        else -> {} // Ignore
                    }
                    builder.addEmptyLine()
                }
            }
            return builder.result()
        }
        private val el = ByteString.fromString("\n")
        private fun ByteStringBuilder.addEmptyLine() = this.append(el)
    }
    override fun serializerForResponse(acceptedMessageProtocols: MutableList<MessageProtocol>) = serializerForRequest()
    override fun serializerForRequest(): MessageSerializer.NegotiatedSerializer<BulkRequest, ByteString> = Serializer(defaultProtocol)
    override fun deserializer(protocol: MessageProtocol?):
        MessageSerializer.NegotiatedDeserializer<BulkRequest, ByteString> = throw NotImplementedError("Unsupported now")
}
