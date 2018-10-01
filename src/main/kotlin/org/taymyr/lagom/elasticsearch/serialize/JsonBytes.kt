package org.taymyr.lagom.elasticsearch.serialize

import org.taymyr.lagom.elasticsearch.DTOAnnotation
import java.util.Arrays

@DTOAnnotation
data class JsonBytes(
    val bytes: ByteArray?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JsonBytes

        if (!Arrays.equals(bytes, other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes?.let { Arrays.hashCode(it) } ?: 0
    }
}