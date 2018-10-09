package org.taymyr.lagom.elasticsearch.document.dsl.bulk

import org.taymyr.lagom.elasticsearch.document.dsl.Document

class BulkRequestFabric {

    private val commands: MutableList<BulkCommand> = mutableListOf()

    fun newCommand() = Request(this)
    fun complete() = BulkRequest(commands)

    class Request(
        private val fabric: BulkRequestFabric
    ) {
        private var id: String? = null
        private var element: Document<*>? = null

        fun forId(id: String) = apply { this.id = id }
        @Throws(IllegalArgumentException::class)
        fun delete() = fabric.apply {
            id?.let { fabric.commands.add(DeleteBulkCommand(it)) }
                ?: throw IllegalArgumentException("id is must be not null")
        }
        fun withElement(element: Document<*>) = apply { this.element = element }
        @Throws(IllegalArgumentException::class)
        fun create() = fabric.apply {
            id?.let { i ->
                element?.let { e ->
                    fabric.commands.add(CreateBulkCommand(i, e))
                } ?: throw IllegalArgumentException("element is must be not null")
            } ?: throw IllegalArgumentException("id is must be not null")
        }
        @Throws(IllegalArgumentException::class)
        fun index() = fabric.apply {
            id?.let { i ->
                element?.let { e ->
                    fabric.commands.add(IndexBulkCommand(i, e))
                } ?: throw IllegalArgumentException("element is must be not null")
            } ?: throw IllegalArgumentException("id is must be not null")
        }
        fun update(ignoreNull: Boolean = true) = fabric.apply {
            id?.let { i ->
                element?.let { e ->
                    fabric.commands.add(UpdateBulkCommand(i, e, ignoreNull))
                } ?: throw IllegalArgumentException("element is must be not null")
            } ?: throw IllegalArgumentException("id is must be not null")
        }
    }
}
