package org.taymyr.lagom.elasticsearch.search.dsl.query

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.taymyr.lagom.elasticsearch.script.Script

interface Sort

@JsonSerialize(using = Order.OrderSerializer::class)
abstract class Order(private val value: String) : Sort {

    abstract val expr: String

    data class AscOrder(override val expr: String) : Order("asc")
    data class DescOrder(override val expr: String) : Order("desc")

    class OrderSerializer : JsonSerializer<Order>() {
        override fun serialize(value: Order?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.run {
                value?.let { order ->
                    writeStartObject()
                    writeObjectField(order.expr, order.value)
                    writeEndObject()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun asc(expr: String) = AscOrder(expr)

        @JvmStatic
        fun desc(expr: String) = DescOrder(expr)
    }
}

enum class SortDirection(@JsonValue val value: String) {
    ASC("asc"), DESC("desc")
}

enum class SortMode(@JsonValue val value: String) {
    MIN("min"), MAX("max"), SUM("sum"), AVG("avg"), MEDIAN("median")
}

data class FieldSortSpec(val order: SortDirection, val mode: SortMode? = null) {
    companion object {
        @JvmOverloads
        @JvmStatic
        fun asc(mode: SortMode? = null) = FieldSortSpec(SortDirection.ASC, mode)

        @JvmOverloads
        @JvmStatic
        fun desc(mode: SortMode? = null) = FieldSortSpec(SortDirection.DESC, mode)
    }
}

@JsonSerialize(using = FieldSort.FieldSortSerializer::class)
data class FieldSort(val field: String, val sort: FieldSortSpec) : Sort {
    class FieldSortSerializer : JsonSerializer<FieldSort>() {
        override fun serialize(value: FieldSort?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.apply {
                value?.let {
                    writeStartObject()
                    writeObjectField(it.field, it.sort)
                    writeEndObject()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun fieldSort(field: String, sort: FieldSortSpec) = FieldSort(field, sort)
    }
}

@JsonSerialize(using = ScriptSort.ScriptSortSerializer::class)
data class ScriptSort(val type: String, val script: Script, val order: SortDirection) : Sort {
    class ScriptSortSerializer : JsonSerializer<ScriptSort>() {
        override fun serialize(value: ScriptSort?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.apply {
                value?.let {
                    writeStartObject()
                    writeFieldName("_script")
                    writeStartObject()
                    writeObjectField("type", it.type)
                    writeObjectField("script", it.script)
                    writeObjectField("order", it.order)
                    writeEndObject()
                    writeEndObject()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun scriptSort(type: String, script: Script, order: SortDirection) = ScriptSort(type, script, order)
    }
}
