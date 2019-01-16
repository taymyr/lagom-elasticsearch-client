package org.taymyr.lagom.elasticsearch.search

import io.kotlintest.eventually
import io.kotlintest.extensions.TestListener
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.seconds
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.kotlintest.whenReady
import org.taymyr.lagom.elasticsearch.LagomClientAndEmbeddedElastic
import org.taymyr.lagom.elasticsearch.SampleDocument
import org.taymyr.lagom.elasticsearch.SampleDocumentResult
import org.taymyr.lagom.elasticsearch.deser.invoke
import org.taymyr.lagom.elasticsearch.deser.invokeT
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.DateRange
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.DateRangeExpression
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.ExistsQuery
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.RangeQuery
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.ChronoUnit.YEARS
import java.util.UUID

class ElasticSearchDateRangeIT : WordSpec() {

    override fun listeners(): List<TestListener> = listOf(Companion)

    init {
        "ElasticSearch" should {
            val indexName = "date-range-test"
            val typeName = "samples"
            "successfully create the index" {
                val request = CreateIndex(
                    CreateIndex.Settings(1, 1),
                    mapOf(
                        typeName to CreateIndex.Mapping(mapOf(
                            "id" to MappingProperty.TEXT,
                            "user" to MappingProperty.KEYWORD,
                            "message" to MappingProperty.KEYWORD,
                            "age" to MappingProperty.INTEGER,
                            "balance" to MappingProperty.DOUBLE,
                            "creationDate" to MappingProperty.DATE
                        ))
                    )
                )
                whenReady(elasticIndices.create(indexName).invoke(request).toCompletableFuture()) {
                    it.acknowledged shouldBe true
                    it.shardsAcknowledged shouldBe true
                    it.index shouldBe indexName
                }
            }
            "successfully index 24 documents with creationDate of 2018-01-01 UTC (one per hour)" {
                repeat(24) {
                    val creationDate = LocalDateTime.now().truncatedTo(DAYS)
                        .withYear(2018).withMonth(1).withDayOfMonth(1).withHour(it)
                    val uuid = UUID.randomUUID()
                    val doc = SampleDocument(
                        user = "thisDayUser-$uuid",
                        message = "messageOfThisDay",
                        creationDate = creationDate
                    )
                    whenReady(elasticDocument.indexWithId(indexName, typeName, uuid.toString())
                        .invoke(doc).toCompletableFuture()) { result ->
                        result.index shouldBe indexName
                        result.type shouldBe typeName
                    }
                }
            }
            "successfully serialize all date range term fields using the LocalDateTime builder variant" {
                val formatter = DateTimeFormatter.ofPattern(DateRange.LOCAL_DATE_TIME_FORMAT)
                val gt = LocalDateTime.now()
                val gte = LocalDateTime.now().plusDays(1)
                val lt = LocalDateTime.now().plusDays(2)
                val lte = LocalDateTime.now().plusDays(3)
                val offset = ZonedDateTime.now().offset
                val range = DateRange.localDateTimeRange()
                    .boost(0.1)
                    .gt(gt).gte(gte).lt(lt).lte(lte).timeZone(offset)
                    .build()
                range.gt shouldBe gt.format(formatter)
                range.gte shouldBe gte.format(formatter)
                range.lt shouldBe lt.format(formatter)
                range.lte shouldBe lte.format(formatter)
                range.boost shouldBe 0.1
                if (ZoneOffset.UTC != offset) {
                    range.timeZone shouldBe offset.toString()
                } else {
                    range.timeZone shouldBe null
                }
                range.format shouldBe DateRange.LOCAL_DATE_TIME_FORMAT
            }
            "successfully find all documents with date '2018-01-01' using the LocalDateTime builder variant" {
                val from = LocalDateTime.now().truncatedTo(DAYS)
                    .withYear(2018).withMonth(1).withDayOfMonth(1)
                val to = LocalDateTime.now().truncatedTo(DAYS)
                    .withYear(2018).withMonth(1).withDayOfMonth(2)
                val dateRange = DateRange.localDateTimeRange().gte(from).lt(to)
                    .timeZone(ZoneOffset.UTC)
                    .build()
                val request = SearchRequest(
                    query = BoolQuery.boolQuery().must(RangeQuery("creationDate", dateRange)).build(),
                    size = 9999
                )
                eventually(5.seconds, AssertionError::class.java) {
                    whenReady(elasticSearch.search(listOf(indexName), listOf(typeName)).invokeT<SearchRequest, SampleDocumentResult>(request)
                        .toCompletableFuture()) { result ->
                        result.hits.hits shouldHaveSize 24
                    }
                }
            }
            "successfully serialize all date range term fields using the ZonedDateTime builder variant" {
                val formatter = DateTimeFormatter.ofPattern(DateRange.ZONED_DATE_TIME_FORMAT)
                val gt = ZonedDateTime.now()
                val gte = ZonedDateTime.now().plusDays(1)
                val lt = ZonedDateTime.now().plusDays(2)
                val lte = ZonedDateTime.now().plusDays(3)
                val range = DateRange.zonedDateTimeRange()
                    .boost(0.1)
                    .gt(gt).gte(gte).lt(lt).lte(lte)
                    .build()
                range.gt shouldBe gt.format(formatter)
                range.gte shouldBe gte.format(formatter)
                range.lt shouldBe lt.format(formatter)
                range.lte shouldBe lte.format(formatter)
                range.boost shouldBe 0.1
                range.timeZone shouldBe null
                range.format shouldBe DateRange.ZONED_DATE_TIME_FORMAT
            }
            "successfully find all documents with date '2018-01-01' using the ZonedDateTime builder variant" {
                val zoneOffset = ZoneOffset.ofHours(3)
                val from = ZonedDateTime.now().withZoneSameLocal(zoneOffset).truncatedTo(DAYS)
                    .withYear(2018).withMonth(1).withDayOfMonth(1).withHour(3)
                val to = ZonedDateTime.now().withZoneSameLocal(zoneOffset).truncatedTo(DAYS)
                    .withYear(2018).withMonth(1).withDayOfMonth(2).withHour(3)
                val dateRange = DateRange.zonedDateTimeRange().gte(from).lt(to)
                    .build()
                val request = SearchRequest(
                    query = BoolQuery.boolQuery().must(RangeQuery("creationDate", dateRange)).build(),
                    size = 9999
                )
                eventually(5.seconds, AssertionError::class.java) {
                    whenReady(elasticSearch.search(listOf(indexName), listOf(typeName)).invokeT<SearchRequest, SampleDocumentResult>(request)
                        .toCompletableFuture()) { result ->
                        result.hits.hits shouldHaveSize 24
                    }
                }
            }
            "successfully serialize all date range term fields using the DateRangeExpression builder variant" {
                val gt = DateRangeExpression.of("2018-01-01").trunc(DAYS)
                val gte = DateRangeExpression.of("2017-01-01").trunc(DAYS)
                val lt = DateRangeExpression.of("2018/01/01").add(1, DAYS).trunc(DAYS)
                    .add(30, SECONDS)
                    .trunc(MINUTES)
                    .trunc(DAYS)
                val lte = DateRangeExpression.ofNow()
                    .add(1, DAYS).trunc(SECONDS)
                    .add(30, HOURS).trunc(YEARS).trunc(MINUTES)
                val range = DateRange.nativeRange().gt(gt).gte(gte).lt(lt).lte(lte)
                    .boost(0.1)
                    .timeZone("+00:00")
                    .format(setOf("yyyy-MM-dd", "yyyy/MM/dd"))
                    .build()
                range.gt shouldBe "2018-01-01||/d"
                range.gte shouldBe "2017-01-01||/d"
                range.lt shouldBe "2018/01/01||+1d/d+30s/m/d"
                range.lte shouldBe "${DateRangeExpression.NOW}+1d/s+30h/y/m"
                range.boost shouldBe 0.1
                range.timeZone shouldBe "+00:00"
                range.format shouldBe "yyyy-MM-dd||yyyy/MM/dd"
            }
            "successfully find all documents with date '2018-01-01' using date math" {
                val from = DateRangeExpression.of("2018-01-01").trunc(DAYS)
                val to = DateRangeExpression.of("2018/01/01").add(1, DAYS).trunc(DAYS)
                    .add(30, SECONDS)
                    .trunc(MINUTES)
                    .trunc(DAYS)
                val dateRange = DateRange.nativeRange().gte(from).lt(to)
                    .format(setOf("yyyy-MM-dd", "yyyy/MM/dd"))
                    .build()
                val request = SearchRequest(
                    query = BoolQuery.boolQuery().must(RangeQuery("creationDate", dateRange)).build(),
                    size = 9999
                )
                eventually(5.seconds, AssertionError::class.java) {
                    whenReady(elasticSearch.search(listOf(indexName), listOf(typeName)).invokeT<SearchRequest, SampleDocumentResult>(request)
                        .toCompletableFuture()) { result ->
                        result.hits.hits shouldHaveSize 24
                    }
                }
            }
            "successfully find documents with field 'user'" {
                val request = SearchRequest(
                    query = ExistsQuery.of("user"),
                    size = 9999
                )
                eventually(5.seconds, AssertionError::class.java) {
                    whenReady(elasticSearch.search().invokeT<SearchRequest, SampleDocumentResult>(request).toCompletableFuture()) { result ->
                        result.hits.hits shouldHaveSize 24
                    }
                }
            }
            "successfully not found documents with field 'useruser'" {
                val request = SearchRequest(
                    query = ExistsQuery.of("useruser")
                )
                eventually(5.seconds, AssertionError::class.java) {
                    whenReady(elasticSearch.search().invokeT<SearchRequest, SampleDocumentResult>(request).toCompletableFuture()) { result ->
                        result.hits.hits shouldHaveSize 0
                    }
                }
            }
        }
    }

    companion object : LagomClientAndEmbeddedElastic()
}