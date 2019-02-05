package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.ChronoUnit.WEEKS
import java.time.temporal.ChronoUnit.YEARS

class DateRangeExpression
private constructor(private var anchor: String = "", private var expression: String = "") {

    private fun mapUnit(unit: ChronoUnit) = when (unit) {
        SECONDS -> "s"
        MINUTES -> "m"
        HOURS -> "h"
        DAYS -> "d"
        WEEKS -> "w"
        MONTHS -> "M"
        YEARS -> "y"
        else -> throw error("Unsupported unit: $unit")
    }

    fun add(count: Int, unit: ChronoUnit) = apply {
        expression += (if (count >= 0) "+" else "") + count + mapUnit(unit)
    }

    fun trunc(unit: ChronoUnit) = apply { expression += "/${mapUnit(unit)}" }

    override fun toString(): String {
        return when (anchor) {
            NOW -> NOW
            else -> "$anchor||"
        } + expression
    }

    companion object {
        @JvmField val NOW = "now"

        @JvmStatic fun ofNow() = DateRangeExpression(NOW)

        @JvmStatic fun of(date: String) = DateRangeExpression(date)
    }
}