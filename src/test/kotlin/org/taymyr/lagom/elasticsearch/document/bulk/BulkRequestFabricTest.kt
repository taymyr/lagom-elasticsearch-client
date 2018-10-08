package org.taymyr.lagom.elasticsearch.document.bulk

import io.kotlintest.matchers.beInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCommand
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequestFabric
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.CreateBulkCommand
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.DeleteBulkCommand
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.IndexBulkCommand
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.UpdateBulkCommand

class BulkRequestFabricTest : WordSpec({
    val testEntity = IndexedSampleDocument(SampleDocument("user", "test"))
    "BulkCommand" should {
        "successfully return id" {
            val test = object : BulkCommand("test") {}
            test.id shouldBe "test"
        }
    }
    "Delete" should {
        "create command successfully" {
            val request = BulkRequestFabric()
                .newCommand().forId("1").delete()
                .complete()
            request.commands.size shouldBe 1
            val command = request.commands[0]
            command shouldBe beInstanceOf(DeleteBulkCommand::class)
            command as DeleteBulkCommand
            command.id shouldBe "1"
        }
        "throw exception on creating without id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().delete().complete() }
        }
    }
    "Create" should {
        "create command successfully" {
            val request = BulkRequestFabric()
                .newCommand().forId("1").withElement(testEntity).create()
                .complete()
            request.commands.size shouldBe 1
            val command = request.commands[0]
            command shouldBe beInstanceOf(CreateBulkCommand::class)
            command as CreateBulkCommand
            command.id shouldBe "1"
            command.element shouldBe testEntity
        }
        "throw exception on creating without id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().create().complete() }
        }
        "throw exception on creating without element but with id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().forId("1").create().complete() }
        }
    }
    "Index" should {
        "create command successfully" {
            val request = BulkRequestFabric()
                .newCommand().forId("1").withElement(testEntity).index()
                .complete()
            request.commands.size shouldBe 1
            val command = request.commands[0]
            command shouldBe beInstanceOf(IndexBulkCommand::class)
            command as IndexBulkCommand
            command.id shouldBe "1"
            command.element shouldBe testEntity
        }
        "throw exception on creating without id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().index().complete() }
        }
        "throw exception on creating without element but with id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().forId("1").index().complete() }
        }
    }
    "Update" should {
        "create command successfully" {
            val request = BulkRequestFabric()
                .newCommand().forId("1").withElement(testEntity).update()
                .newCommand().forId("2").withElement(testEntity).update(false)
                .complete()
            request.commands.size shouldBe 2
            val command0 = request.commands[0]
            val command1 = request.commands[1]
            command0 shouldBe beInstanceOf(UpdateBulkCommand::class)
            command1 shouldBe beInstanceOf(UpdateBulkCommand::class)
            command0 as UpdateBulkCommand
            command1 as UpdateBulkCommand
            command0.id shouldBe "1"
            command1.id shouldBe "2"
            command0.element shouldBe testEntity
            command1.element shouldBe testEntity
            command0.ignoreNull shouldBe true
            command1.ignoreNull shouldBe false
        }
        "throw exception on creating without id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().update().complete() }
        }
        "throw exception on creating without element but with id" {
            shouldThrow<IllegalArgumentException> { BulkRequestFabric().newCommand().forId("1").update().complete() }
        }
    }
})
