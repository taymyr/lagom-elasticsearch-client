package org.taymyr.lagom.elasticsearch.search;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.AbstractElasticsearchIT;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocumentResult;
import org.taymyr.lagom.elasticsearch.document.dsl.IndexDocumentResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndex.Settings;
import org.taymyr.lagom.elasticsearch.indices.dsl.CreateIndexResult;
import org.taymyr.lagom.elasticsearch.indices.dsl.Mapping;
import org.taymyr.lagom.elasticsearch.indices.dsl.MappingProperty;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.DateRange;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.DateRangeExpression;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.ExistsQuery;
import org.taymyr.lagom.elasticsearch.search.dsl.query.term.RangeQuery;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invoke;
import static org.taymyr.lagom.elasticsearch.deser.ServiceCallKt.invokeT;

import static java.lang.Thread.sleep;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;

class ElasticSearchDateRangeIT extends AbstractElasticsearchIT {

    private String indexName = "date-range-test";
    private String typeName = "samples";

    @Test
    void shouldBeCorrect() throws InterruptedException, ExecutionException, TimeoutException {
        createIndex();
        generateData();
        sleep(2000);
        searchLocalDateTime();
        searchZonedDateTime();
        searchDateMath();
        searchUser();
        searchUserUser();
    }

    void createIndex() throws InterruptedException, ExecutionException, TimeoutException {
        CreateIndex request = new CreateIndex(
                new Settings(1, 1),
                ImmutableMap.of(
                        typeName, new Mapping(ImmutableMap.<String, MappingProperty>builder()
                                .put("id", MappingProperty.TEXT)
                                .put("user", MappingProperty.KEYWORD)
                                .put("message", MappingProperty.KEYWORD)
                                .put("age", MappingProperty.INTEGER)
                                .put("balance", MappingProperty.DOUBLE)
                                .put("creationDate", MappingProperty.DATE)
                                .build()
                        ))
        );
        CreateIndexResult result = eventually(elasticIndices.create(indexName).invoke(request));
        assertThat(result.getIndex()).isEqualTo(indexName);
        assertThat(result.getAcknowledged()).isTrue();
        assertThat(result.getShardsAcknowledged()).isTrue();
    }

    // successfully index 24 documents with creationDate of 2018-01-01 UTC (one per hour)
    void generateData() throws InterruptedException, ExecutionException, TimeoutException {
        for (int it = 0; it < 24; ++it) {
            LocalDateTime creationDate = LocalDateTime.now().truncatedTo(DAYS)
                    .withYear(2018).withMonth(1).withDayOfMonth(1).withHour(it);
            UUID uuid = UUID.randomUUID();
            SampleDocument doc = new SampleDocument(
                    "thisDayUser-$uuid",
                    "messageOfThisDay",
                    null,
                    null,
                    creationDate
            );
            IndexDocumentResult result = eventually(invoke(elasticDocument.indexWithId(indexName, typeName, uuid.toString()), doc));
            assertThat(result.getIndex()).isEqualTo(indexName);
            assertThat(result.getType()).isEqualTo(typeName);
        }
    }

    // successfully find all documents with date '2018-01-01' using the LocalDateTime builder variant
    void searchLocalDateTime() throws InterruptedException, ExecutionException, TimeoutException {
        LocalDateTime from = LocalDateTime.now().truncatedTo(DAYS)
            .withYear(2018).withMonth(1).withDayOfMonth(1);
        LocalDateTime to = LocalDateTime.now().truncatedTo(DAYS)
            .withYear(2018).withMonth(1).withDayOfMonth(2);
        DateRange dateRange = DateRange.localDateTimeRange().gte(from).lt(to)
            .timeZone(ZoneOffset.UTC)
            .build();
        SearchRequest request = new SearchRequest(
            BoolQuery.boolQuery().must(new RangeQuery("creationDate", dateRange)).build(),
            null,
            9999
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName, typeName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(24);
    }

    // successfully find all documents with date '2018-01-01' using the ZonedDateTime builder variant
    void searchZonedDateTime() throws InterruptedException, ExecutionException, TimeoutException {
        ZoneOffset zoneOffset = ZoneOffset.ofHours(3);
        ZonedDateTime from = ZonedDateTime.now().withZoneSameLocal(zoneOffset).truncatedTo(DAYS)
            .withYear(2018).withMonth(1).withDayOfMonth(1).withHour(3);
        ZonedDateTime to = ZonedDateTime.now().withZoneSameLocal(zoneOffset).truncatedTo(DAYS)
            .withYear(2018).withMonth(1).withDayOfMonth(2).withHour(3);
        DateRange dateRange = DateRange.zonedDateTimeRange().gte(from).lt(to)
            .build();
        SearchRequest request = new SearchRequest(
            BoolQuery.boolQuery().must(new RangeQuery("creationDate", dateRange)).build(),
            null,
            9999
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName, typeName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(24);
    }

    // successfully find all documents with date '2018-01-01' using date math
    void searchDateMath() throws InterruptedException, ExecutionException, TimeoutException {
        DateRangeExpression from = DateRangeExpression.of("2018-01-01").trunc(DAYS);
        DateRangeExpression to = DateRangeExpression.of("2018/01/01").add(1, DAYS).trunc(DAYS)
            .add(30, SECONDS)
            .trunc(MINUTES)
            .trunc(DAYS);
        DateRange dateRange = DateRange.nativeRange().gte(from).lt(to)
            .format(ImmutableSet.of("yyyy-MM-dd", "yyyy/MM/dd"))
            .build();
        SearchRequest request = new SearchRequest(
            BoolQuery.boolQuery().must(new RangeQuery("creationDate", dateRange)).build(),
            null,
            9999
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(indexName, typeName), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(24);
    }

    // successfully find documents with field 'user'
    void searchUser() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
            ExistsQuery.of("user"),
            null,
            9999
        );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).hasSize(24);
    }

    // successfully not found documents with field 'useruser'
    void searchUserUser() throws InterruptedException, ExecutionException, TimeoutException {
        SearchRequest request = new SearchRequest(
                    ExistsQuery.of("useruser")
                );
        SampleDocumentResult result = eventually(invokeT(elasticSearch.search(), request, SampleDocumentResult.class));
        assertThat(result.getHits().getHits()).isEmpty();
    }


    @Test
    @DisplayName("successfully serialize all date range term fields using the LocalDateTime builder variant")
    void shouldSuccessfullySerializeLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateRange.LOCAL_DATE_TIME_FORMAT);
        LocalDateTime gt = LocalDateTime.now();
        LocalDateTime gte = LocalDateTime.now().plusDays(1);
        LocalDateTime lt = LocalDateTime.now().plusDays(2);
        LocalDateTime lte = LocalDateTime.now().plusDays(3);
        ZoneOffset offset = ZonedDateTime.now().getOffset();
        DateRange range = DateRange.localDateTimeRange()
                .boost(0.1)
                .gt(gt).gte(gte).lt(lt).lte(lte).timeZone(offset)
                .build();
        assertThat(range.getGt()).isEqualTo(gt.format(formatter));
        assertThat(range.getGte()).isEqualTo(gte.format(formatter));
        assertThat(range.getLt()).isEqualTo(lt.format(formatter));
        assertThat(range.getLte()).isEqualTo(lte.format(formatter));
        assertThat(range.getBoost()).isEqualTo(0.1);
        if (ZoneOffset.UTC != offset) {
            assertThat(range.getTimeZone()).isEqualTo(offset.toString());
        } else {
            assertThat(range.getTimeZone()).isNull();
        }
        assertThat(range.getFormat()).isEqualTo(DateRange.LOCAL_DATE_TIME_FORMAT);
    }

    @Test
    @DisplayName("successfully serialize all date range term fields using the ZonedDateTime builder variant")
    void shouldSuccessfullySerializeZonedDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateRange.ZONED_DATE_TIME_FORMAT);
        ZonedDateTime gt = ZonedDateTime.now();
        ZonedDateTime gte = ZonedDateTime.now().plusDays(1);
        ZonedDateTime lt = ZonedDateTime.now().plusDays(2);
        ZonedDateTime lte = ZonedDateTime.now().plusDays(3);
        DateRange range = DateRange.zonedDateTimeRange()
            .boost(0.1)
            .gt(gt).gte(gte).lt(lt).lte(lte)
            .build();
        assertThat(range.getGt()).isEqualTo(gt.format(formatter));
        assertThat(range.getGte()).isEqualTo(gte.format(formatter));
        assertThat(range.getLt()).isEqualTo(lt.format(formatter));
        assertThat(range.getLte()).isEqualTo(lte.format(formatter));
        assertThat(range.getBoost()).isEqualTo(0.1);
        assertThat(range.getTimeZone()).isNull();
        assertThat(range.getFormat()).isEqualTo(DateRange.ZONED_DATE_TIME_FORMAT);
    }

    @Test
    @DisplayName("successfully serialize all date range term fields using the DateRangeExpression builder variant")
    void shouldSuccessfullySerializeDateRangeExpression() {
        DateRangeExpression gt = DateRangeExpression.of("2018-01-01").trunc(DAYS);
        DateRangeExpression gte = DateRangeExpression.of("2017-01-01").trunc(DAYS);
        DateRangeExpression lt = DateRangeExpression.of("2018/01/01").add(1, DAYS).trunc(DAYS)
            .add(30, SECONDS)
            .trunc(MINUTES)
            .trunc(DAYS);
        DateRangeExpression lte = DateRangeExpression.ofNow()
            .add(1, DAYS).trunc(SECONDS)
            .add(30, HOURS).trunc(YEARS).trunc(MINUTES);
        DateRange range = DateRange.nativeRange().gt(gt).gte(gte).lt(lt).lte(lte)
            .boost(0.1)
            .timeZone("+00:00")
            .format(ImmutableSet.of("yyyy-MM-dd", "yyyy/MM/dd"))
            .build();
        assertThat(range.getGt()).isEqualTo("2018-01-01||/d");
        assertThat(range.getGte()).isEqualTo("2017-01-01||/d");
        assertThat(range.getLt()).isEqualTo("2018/01/01||+1d/d+30s/m/d");
        assertThat(range.getLte()).isEqualTo(DateRangeExpression.NOW + "+1d/s+30h/y/m");
        assertThat(range.getBoost()).isEqualTo(0.1);
        assertThat(range.getTimeZone()).isEqualTo("+00:00");
        assertThat(range.getFormat()).isEqualTo("yyyy-MM-dd||yyyy/MM/dd");
    }
}