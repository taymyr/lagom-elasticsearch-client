package org.taymyr.lagom.elasticsearch.client;

import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.deser.PathParamSerializers;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.taymyr.lagom.elasticsearch.client.serialize.JsonBytes;
import org.taymyr.lagom.elasticsearch.client.serialize.JsonBytesSerializer;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.restCall;

public interface ElasticSearch extends ElasticSearchKt {

    @Override
    default Descriptor descriptor() {
        return named("elastic-search")
                .withCalls(
                        restCall(Method.GET, "/:index/:type/_search", this::search),
                        restCall(Method.POST, "/:index/:type/:id/_update", this::updateIndex),
                        restCall(Method.DELETE, "/:index/:type/:id", this::deleteById)
                ).withPathParamSerializer(String.class, PathParamSerializers.STRING)
                .withMessageSerializer(JsonBytes.class, new JsonBytesSerializer())
                .withAutoAcl(true);
    }

}
