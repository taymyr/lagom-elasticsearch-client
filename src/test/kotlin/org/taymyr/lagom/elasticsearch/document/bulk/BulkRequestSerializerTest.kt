package org.taymyr.lagom.elasticsearch.document.bulk

import com.lightbend.lagom.javadsl.api.transport.MessageProtocol
import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequestFabric
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequestSerializer
import java.nio.charset.Charset
import java.util.Optional

class BulkRequestSerializerTest : WordSpec({
    val testProtocol = MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty())
    val testEntity = IndexedSampleDocument(SampleDocument("user", "test"))
    "BulkRequestSerializer" should {
        "throwable on creating deserializer" {
            shouldThrow<NotImplementedError> { BulkRequestSerializer().deserializer(testProtocol) }
        }
        "create serializer on executing serializerForRequest()" {
            val serializer = BulkRequestSerializer().serializerForRequest()
            serializer shouldBe beInstanceOf(BulkRequestSerializer.Serializer::class)
        }
        "create serializer on executing serializerForResponse()" {
            val serializer = BulkRequestSerializer().serializerForResponse(mutableListOf(testProtocol))
            serializer shouldBe beInstanceOf(BulkRequestSerializer.Serializer::class)
            serializer.protocol() shouldBe testProtocol
        }
    }
    "Delete" should {
        "serialize to json successfully" {
            val request = BulkRequestFabric().newCommand().forId("1").delete().complete()
            val serializer = BulkRequestSerializer().serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            result.trimEnd() shouldBe """{"delete":{"_id":"1"}}"""
        }
    }
    "Create" should {
        "serialize to json successfully" {
            val request = BulkRequestFabric().newCommand().forId("1").withElement(testEntity).create().complete()
            val serializer = BulkRequestSerializer().serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"create":{"_id":"1"}}
                |{"user":"user","message":"test"}
            """.trimMargin()
            result.trimEnd() shouldBe need
        }
    }
    "Index" should {
        "serialize to json successfully" {
            val request = BulkRequestFabric().newCommand().forId("1").withElement(testEntity).index().complete()
            val serializer = BulkRequestSerializer().serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"index":{"_id":"1"}}
                |{"user":"user","message":"test"}
            """.trimMargin()
            result.trimEnd() shouldBe need
        }
    }
    "Update" should {
        "serialize to json successfully" {
            val testEntityWithNull = IndexedSampleDocument(SampleDocument("test", null))
            val request = BulkRequestFabric()
                .newCommand().forId("1").withElement(testEntityWithNull).update()
                .newCommand().forId("2").withElement(testEntityWithNull).update(false)
                .complete()
            val serializer = BulkRequestSerializer().serializerForRequest()
            val result = serializer.serialize(request).decodeString(Charset.defaultCharset())
            val need = """{"update":{"_id":"1"}}
                |{"doc":{"user":"test"}}
                |{"update":{"_id":"2"}}
                |{"doc":{"user":"test","message":null}}
            """.trimMargin()
            result.trimEnd() shouldBe need
        }
    }
})
