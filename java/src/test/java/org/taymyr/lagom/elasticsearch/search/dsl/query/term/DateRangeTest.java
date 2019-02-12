package org.taymyr.lagom.elasticsearch.search.dsl.query.term;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchRequest;
import org.taymyr.lagom.elasticsearch.search.dsl.query.compound.BoolQuery;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.taymyr.lagom.elasticsearch.Helpers.resourceAsString;
import static org.taymyr.lagom.elasticsearch.Helpers.serializeRequest;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.YEARS;

class DateRangeTest {

    @Test
    @DisplayName("successfully serialize search request with LocalDateTime range")
    void shouldSuccessfullySerializeLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
        LocalDateTime gt = dateTime;
        LocalDateTime gte = dateTime.plusDays(1);
        LocalDateTime lt = dateTime.plusDays(2);
        LocalDateTime lte = dateTime.plusDays(3);
        ZoneOffset offset = ZoneOffset.ofHours(3);
        DateRange range = DateRange.localDateTimeBuilder()
                .boost(0.1)
                .gt(gt).gte(gte).lt(lt).lte(lte).timeZone(offset)
                .build();
        SearchRequest request = new SearchRequest(
            BoolQuery.builder().must(new RangeQuery("date", range)).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/local_date_time.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with ZonedDateTime range")
    void searchZonedDateTime() {
        ZoneOffset offset = ZoneOffset.ofHours(3);
        LocalDateTime dateTime = LocalDateTime.of(2000, 1, 1, 1, 1, 1);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, offset);
        ZonedDateTime gt = zonedDateTime;
        ZonedDateTime gte = zonedDateTime.plusDays(1);
        ZonedDateTime lt = zonedDateTime.plusDays(2);
        ZonedDateTime lte = zonedDateTime.plusDays(3);
        DateRange range = DateRange.zonedDateTimeBuilder()
                .boost(0.1)
                .gt(gt).gte(gte).lt(lt).lte(lte)
                .build();
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().must(new RangeQuery("date", range)).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/zoned_date_time.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("successfully serialize search request with DateRangeExpression builder")
    void shouldSuccessfullySerializeDateRangeExpression() {
        DateRangeExpression gt = DateRangeExpression.of("2018-01-01").trunc(DAYS);
        DateRangeExpression gte = DateRangeExpression.of("2017-01-01").trunc(DAYS);
        DateRangeExpression lt = DateRangeExpression.of("2018/01/01").add(1, DAYS).trunc(DAYS)
                .add(30, SECONDS)
                .trunc(MINUTES)
                .trunc(DAYS);
        DateRangeExpression lte = DateRangeExpression.NOW
                .add(1, DAYS).trunc(SECONDS)
                .add(30, HOURS).trunc(YEARS).trunc(MINUTES);
        DateRange range = DateRange.nativeBuilder().gt(gt).gte(gte).lt(lt).lte(lte)
                .boost(0.1)
                .timeZone(ZoneOffset.ofHours(0))
                .format("yyyy-MM-dd", "yyyy/MM/dd")
                .build();
        SearchRequest request = new SearchRequest(
                BoolQuery.builder().must(RangeQuery.of("date", range)).build()
        );
        String actual = serializeRequest(request, SearchRequest.class);
        String expected = resourceAsString("org/taymyr/lagom/elasticsearch/search/dsl/query/term/date_expression.json");
        assertThatJson(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("throw exception for incorrect request")
    void shouldThrowExceptionForIncorrectRequest() {
        assertThatThrownBy(() -> DateRange.localDateTimeBuilder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("One of the 'gte', 'gt', 'lt', 'lte' should be specified");
        assertThatThrownBy(() -> DateRange.zonedDateTimeBuilder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("One of the 'gte', 'gt', 'lt', 'lte' should be specified");
        assertThatThrownBy(() -> DateRange.nativeBuilder().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("One of the 'gte', 'gt', 'lt', 'lte' should be specified");
    }

}