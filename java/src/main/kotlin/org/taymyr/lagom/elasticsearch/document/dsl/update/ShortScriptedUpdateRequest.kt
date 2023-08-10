package org.taymyr.lagom.elasticsearch.document.dsl.update

import org.taymyr.lagom.elasticsearch.script.Script

data class ShortScriptedUpdateRequest(val script: Script) : UpdateRequest()
