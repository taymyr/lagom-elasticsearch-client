package org.taymyr.lagom.elasticsearch.document.dsl.update

data class ShortScriptedUpdateRequest(val script: String) : UpdateRequest()