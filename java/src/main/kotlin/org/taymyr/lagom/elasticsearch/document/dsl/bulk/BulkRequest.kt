package org.taymyr.lagom.elasticsearch.document.dsl.bulk

data class BulkRequest(val commands: List<BulkCommand>) {
    companion object {
        @JvmStatic fun of(commands: List<BulkCommand>) = BulkRequest(commands)
        @JvmStatic fun of(vararg commands: BulkCommand) = BulkRequest(commands.asList())
    }
}
