package org.taymyr.lagom.elasticsearch.deser

import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkDelete
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkIndex
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkUpdate
import java.nio.charset.Charset
import java.util.Optional

class BulkRequestSerializerTest : WordSpec({
    val testProtocol = MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty())
    val testEntity = IndexedSampleDocument(SampleDocument("user", "test"))
    val mapper = ElasticSerializerFactory().mapper
    "BulkRequestSerializer" should {
        "throwable on creating deserializer" {
            shouldThrow<NotImplementedError> { BulkRequestSerializer(mapper).deserializer(testProtocol) }
        }
        "create serializer on executing serializerForRequest()" {
            val serializer = BulkRequestSerializer(mapper).serializerForRequest()
            serializer shouldBe beInstanceOf(BulkRequestSerializer.Serializer::class)
        }
        "create serializer on executing serializerForResponse()" {
            val serializer = BulkRequestSerializer(mapper).serializerForResponse(mutableListOf(testProtocol))
            serializer shouldBe beInstanceOf(BulkRequestSerializer.Serializer::class)
            serializer.protocol() shouldBe testProtocol
        }
    }
    "Delete" should {
        "serialize to json successfully" {
            val request = BulkRequest.ofCommands(BulkDelete("1"))
            val serializer = BulkRequestSerializer(mapper).serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            result.trimEnd() shouldBe """{"delete":{"_id":"1"}}"""
        }
    }
    "Create" should {
        "serialize to json successfully" {
            val request = BulkRequest.ofCommands(BulkCreate("1", testEntity))
            val serializer = BulkRequestSerializer(mapper).serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"create":{"_id":"1"}}
                |{"user":"user","message":"test"}
            """.trimMargin()
            result.trimEnd() shouldBe need
        }
    }
    "Index" should {
        "serialize to json successfully" {
            val request = BulkRequest.ofCommands(BulkIndex("1", testEntity))
            val serializer = BulkRequestSerializer(mapper).serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"index":{"_id":"1"}}
                |{"user":"user","message":"test"}
            """.trimMargin()
            result.trimEnd() shouldBe need
        }
    }
    // TODO(Ilya Korshunov) rewrite tests
    "Update" should {
        "serialize to json successfully" {
            val testEntityWithNull = IndexedSampleDocument(SampleDocument("test", null))
            val request = BulkRequest.ofCommands(BulkUpdate("1", testEntityWithNull))
            val serializer = BulkRequestSerializer(mapper).serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"update":{"_id":"1"}}
                |{"doc":{"user":"test","message":null}}""".trimMargin()
            result.trimEnd() shouldBe need
        }
    }
})
