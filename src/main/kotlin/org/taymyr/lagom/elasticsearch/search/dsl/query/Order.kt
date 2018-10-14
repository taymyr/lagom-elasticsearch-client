package org.taymyr.lagom.elasticsearch.search.dsl.query

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = Order.OrderSerializer::class)
abstract class Order(private val value: String) {

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
        @JvmStatic fun asc(expr: String) = AscOrder(expr)
        @JvmStatic fun desc(expr: String) = DescOrder(expr)
    }
}