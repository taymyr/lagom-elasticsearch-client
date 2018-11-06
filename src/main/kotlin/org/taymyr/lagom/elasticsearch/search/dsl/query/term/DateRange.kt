package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-range-query.html#ranges-on-dates)
 */
data class DateRange(
    val gte: String?,
    val gt: String?,
    val lt: String?,
    val lte: String?,
    val boost: Double?,
    val format: String?,
    @JsonProperty("time_zone")
    val timeZone: String?
) {
    abstract class AbstractBuilder<DateTimeType : Any, TimeZoneType : Any> {
        protected var gte: DateTimeType? = null
        protected var gt: DateTimeType? = null
        protected var lt: DateTimeType? = null
        protected var lte: DateTimeType? = null
        protected var boost: Double? = null
        protected var format: String? = null
        protected var timeZone: TimeZoneType? = null

        fun gte(gte: DateTimeType?) = apply { this.gte = gte }
        fun gt(gt: DateTimeType?) = apply { this.gt = gt }
        fun lt(lt: DateTimeType?) = apply { this.lt = lt }
        fun lte(lte: DateTimeType?) = apply { this.lte = lte }
        fun boost(boost: Double?) = apply { this.boost = boost }
        fun format(patterns: Set<String>) = apply { this.format = patterns.joinToString("||") }
        fun timeZone(timeZone: TimeZoneType) = apply { this.timeZone = timeZone }

        protected fun checkAtLeastOnePredicatePresence() {
            if (listOfNotNull(gte, gt, lt, lte).isEmpty()) {
                throw error("One of the 'gte', 'gt', 'lt', 'lte' should be specified")
            }
        }
        abstract fun build(): DateRange
    }

    class LocalDateTimeRangeBuilder : AbstractBuilder<LocalDateTime, ZoneOffset>() {

        private fun format(src: LocalDateTime?, fmt: String?) = src?.format(DateTimeFormatter.ofPattern(fmt))

        override fun build(): DateRange {
            checkAtLeastOnePredicatePresence()
            if (format == null) {
                format = DEFAULT_FORMAT
            }
            return DateRange(
                gte = format(gte, format),
                gt = format(gt, format),
                lt = format(lt, format),
                lte = format(lte, format),
                boost = boost,
                format = format,
                timeZone = if (ZoneOffset.UTC == timeZone) null else timeZone.toString()
            )
        }
    }

    class NativeRangeBuilder : AbstractBuilder<DateRangeExpression, String>() {

        override fun build(): DateRange {
            checkAtLeastOnePredicatePresence()
            return DateRange(
                gte = gte?.toString(),
                gt = gt?.toString(),
                lt = lt?.toString(),
                lte = lte?.toString(),
                boost = boost,
                format = format,
                timeZone = timeZone
            )
        }
    }

    companion object {
        @JvmField val DEFAULT_FORMAT = "yyyy.MM.dd'T'HH:mm:ss"

        @JvmStatic fun localDateTimeRange() = LocalDateTimeRangeBuilder()
        @JvmStatic fun nativeRange() = NativeRangeBuilder()
    }
}