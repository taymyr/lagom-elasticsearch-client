package org.taymyr.lagom.elasticsearch.document.dsl.bulk

data class BulkRequest(val commands: List<BulkCommand>) {
    companion object {
        @JvmStatic fun ofCommands(commands: List<BulkCommand>) = BulkRequest(commands)
        @JvmStatic fun ofCommands(vararg commands: BulkCommand) = BulkRequest(commands.asList())
    }
}
