package org.taymyr.lagom.elasticsearch.deser;

import akka.util.ByteString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer.NegotiatedSerializer;
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol;
import kotlin.NotImplementedError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.taymyr.lagom.elasticsearch.IndexedSampleDocument;
import org.taymyr.lagom.elasticsearch.SampleDocument;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkCreate;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkDelete;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkIndex;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkRequest;
import org.taymyr.lagom.elasticsearch.document.dsl.bulk.BulkUpdate;

import java.nio.charset.Charset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static java.util.Arrays.asList;

class BulkRequestSerializerTest {

    private MessageProtocol testProtocol = new MessageProtocol(Optional.of("application/json"), Optional.of("utf-8"), Optional.empty());
    private IndexedSampleDocument testEntity = new IndexedSampleDocument(new SampleDocument("user", "test"));
    private ObjectMapper mapper = new ElasticSerializerFactory().getMapper();

    @Test
    @DisplayName("BulkRequestSerializer should throwable on creating deserializer")
    void shouldThrowWhenCreatingDeserializer() {
        assertThatThrownBy(() -> new BulkRequestSerializer(mapper).deserializer(testProtocol))
                .isInstanceOf(NotImplementedError.class);
    }

    @Test
    @DisplayName("BulkRequestSerializer should create serializer on executing serializerForRequest")
    void shouldCreateSerializerOnExecutingSerializerForRequest() {
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForRequest();
        assertThat(serializer).isInstanceOf(BulkRequestSerializer.Serializer.class);
    }

    @Test
    @DisplayName("BulkRequestSerializer should create serializer on executing serializerForResponse")
    void shouldCreateSerializerOnExecutingSerializerForResponse() {
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForResponse(asList(testProtocol));
        assertThat(serializer).isInstanceOf(BulkRequestSerializer.Serializer.class);
        assertThat(serializer.protocol()).isEqualTo(testProtocol);
    }

    @Test
    @DisplayName("Successfully serialize bulk delete")
    void shouldSuccessfullySerializeBulkDelete() {
        BulkRequest request = BulkRequest.ofCommands(new BulkDelete("1"));
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForRequest();
        String result = serializer.serialize(request).decodeString(Charset.defaultCharset());
        assertThat(result.trim()).isEqualTo("{\"delete\":{\"_id\":\"1\"}}");
    }

    @Test
    @DisplayName("Successfully serialize bulk create")
    void shouldSuccessfullySerializeBulkCreate() {
        BulkRequest request = BulkRequest.ofCommands(new BulkCreate("1", testEntity));
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForRequest();
        String result = serializer.serialize(request).decodeString(Charset.defaultCharset());
        String need = "{\"create\":{\"_id\":\"1\"}}\n" +
                "{\"user\":\"user\",\"message\":\"test\"}";
        assertThat(result.trim()).isEqualTo(need);
    }

    @Test
    @DisplayName("Successfully serialize bulk index")
    void shouldSuccessfullySerializeBulkIndex() {
        BulkRequest request = BulkRequest.ofCommands(new BulkIndex("1", testEntity));
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForRequest();
        String result = serializer.serialize(request).decodeString(Charset.defaultCharset());
        String need = "{\"index\":{\"_id\":\"1\"}}\n" +
                "{\"user\":\"user\",\"message\":\"test\"}";
        assertThat(result.trim()).isEqualTo(need);
    }

    @Test
    @DisplayName("Successfully serialize bulk update")
    void shouldSuccessfullySerializeBulkUpdate() {
        IndexedSampleDocument testEntityWithNull = new IndexedSampleDocument(new SampleDocument("test", null));
        BulkRequest request = BulkRequest.ofCommands(new BulkUpdate("1", testEntityWithNull));
        NegotiatedSerializer<BulkRequest, ByteString> serializer = new BulkRequestSerializer(mapper).serializerForRequest();
        String result = serializer.serialize(request).decodeString(Charset.defaultCharset());
        String need = "{\"update\":{\"_id\":\"1\"}}\n" +
                "{\"doc\":{\"user\":\"test\",\"message\":null,\"age\":null,\"balance\":null,\"creationDate\":null}}";
        assertThat(result.trim()).isEqualTo(need);
    }
}
