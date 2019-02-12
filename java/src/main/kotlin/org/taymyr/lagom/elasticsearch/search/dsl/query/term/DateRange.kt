package org.taymyr.lagom.elasticsearch.search.dsl.query.term

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private const val LOCAL_DATE_TIME_FORMAT = "yyyy.MM.dd'T'HH:mm:ss"
private const val ZONED_DATE_TIME_FORMAT = "yyyy.MM.dd'T'HH:mm:ssZ"

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
) : Range {

    abstract class AbstractBuilder<DateTimeType : Any, TimeZoneType : Any> {
        protected var gte: DateTimeType? = null
        protected var gt: DateTimeType? = null
        protected var lt: DateTimeType? = null
        protected var lte: DateTimeType? = null
        protected var boost: Double? = null
        protected var format: String? = null
        protected var timeZone: TimeZoneType? = null

        protected fun checkAtLeastOnePredicatePresence() {
            if (sequenceOf(gte, gt, lt, lte).all { it == null }) {
                throw error("One of the 'gte', 'gt', 'lt', 'lte' should be specified")
            }
        }
        abstract fun build(): DateRange
    }

    class LocalDateTimeRangeBuilder : AbstractBuilder<LocalDateTime, ZoneOffset>() {

        private fun format(src: LocalDateTime?, fmt: String?) = src?.format(DateTimeFormatter.ofPattern(fmt))

        fun gte(gte: LocalDateTime) = apply { this.gte = gte }
        fun gt(gt: LocalDateTime) = apply { this.gt = gt }
        fun lt(lt: LocalDateTime) = apply { this.lt = lt }
        fun lte(lte: LocalDateTime) = apply { this.lte = lte }
        fun boost(boost: Double) = apply { this.boost = boost }
        fun timeZone(timeZone: ZoneOffset) = apply { this.timeZone = timeZone }

        override fun build(): DateRange {
            checkAtLeastOnePredicatePresence()
            format = LOCAL_DATE_TIME_FORMAT
            return DateRange(
                gte = format(gte, format),
                gt = format(gt, format),
                lt = format(lt, format),
                lte = format(lte, format),
                boost = boost,
                format = format,
                timeZone = if (ZoneOffset.UTC == timeZone) null else timeZone?.toString()
            )
        }
    }

    class ZonedDateTimeRangeBuilder : AbstractBuilder<ZonedDateTime, ZoneOffset>() {

        private fun format(src: ZonedDateTime?, fmt: String?) = src?.format(DateTimeFormatter.ofPattern(fmt))

        fun gte(gte: ZonedDateTime) = apply { this.gte = gte }
        fun gt(gt: ZonedDateTime) = apply { this.gt = gt }
        fun lt(lt: ZonedDateTime) = apply { this.lt = lt }
        fun lte(lte: ZonedDateTime) = apply { this.lte = lte }
        fun boost(boost: Double) = apply { this.boost = boost }

        override fun build(): DateRange {
            checkAtLeastOnePredicatePresence()
            format = ZONED_DATE_TIME_FORMAT
            return DateRange(
                gte = format(gte, format),
                gt = format(gt, format),
                lt = format(lt, format),
                lte = format(lte, format),
                boost = boost,
                format = format,
                timeZone = null
            )
        }
    }

    class NativeRangeBuilder : AbstractBuilder<DateRangeExpression, ZoneOffset>() {

        fun gte(gte: DateRangeExpression) = apply { this.gte = gte }
        fun gt(gt: DateRangeExpression) = apply { this.gt = gt }
        fun lt(lt: DateRangeExpression) = apply { this.lt = lt }
        fun lte(lte: DateRangeExpression) = apply { this.lte = lte }
        fun boost(boost: Double) = apply { this.boost = boost }
        fun format(vararg formats: String) = apply { this.format = formats.joinToString("||") }
        fun timeZone(timeZone: ZoneOffset) = apply { this.timeZone = timeZone }

        override fun build(): DateRange {
            checkAtLeastOnePredicatePresence()
            return DateRange(
                gte = gte?.toString(),
                gt = gt?.toString(),
                lt = lt?.toString(),
                lte = lte?.toString(),
                boost = boost,
                format = format,
                timeZone = timeZone?.toString()
            )
        }
    }

    companion object {
        @JvmStatic fun localDateTimeBuilder() = LocalDateTimeRangeBuilder()
        @JvmStatic fun zonedDateTimeBuilder() = ZonedDateTimeRangeBuilder()
        @JvmStatic fun nativeBuilder() = NativeRangeBuilder()
    }
}