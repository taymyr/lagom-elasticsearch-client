package org.taymyr.lagom.elasticsearch.deser

import akka.NotUsed
import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.ServiceCall
import java.util.concurrent.CompletionStage

val elasticSerializerFactory = ElasticSerializerFactory()

fun <TypedRequest : Any, Response : Any> ServiceCall<ByteString, Response>.invoke(data: TypedRequest): CompletionStage<Response> =
    invoke(elasticSerializerFactory.messageSerializerFor<TypedRequest>(data::class.java).serializerForRequest().serialize(data))

inline fun <reified TypedResponse : Any> ServiceCall<NotUsed, ByteString>.invokeT(): CompletionStage<TypedResponse> =
    invoke().thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(TypedResponse::class.java)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }

inline fun <reified Request : Any, reified TypedResponse : Any> ServiceCall<Request, ByteString>.invokeT(data: Request): CompletionStage<TypedResponse> =
    invoke(data).thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(TypedResponse::class.java)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }

inline fun <reified TypedRequest : Any, reified TypedResponse : Any> ServiceCall<ByteString, ByteString>.invokeTT(data: TypedRequest): CompletionStage<TypedResponse> =
    invoke(elasticSerializerFactory.messageSerializerFor<TypedRequest>(data::class.java).serializerForRequest().serialize(data)).thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(TypedResponse::class.java)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }

fun <TypedResponse : Any> ServiceCall<NotUsed, ByteString>.invokeT(clazz: Class<TypedResponse>): CompletionStage<TypedResponse> =
    invoke().thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(clazz)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }

fun <Request : Any, TypedResponse : Any> ServiceCall<Request, ByteString>.invokeT(data: Request, clazz: Class<TypedResponse>): CompletionStage<TypedResponse> =
    invoke(data).thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(clazz)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }

fun <TypedRequest : Any, TypedResponse : Any> ServiceCall<ByteString, ByteString>.invokeTT(data: TypedRequest, clazz: Class<TypedResponse>): CompletionStage<TypedResponse> =
    invoke(elasticSerializerFactory.messageSerializerFor<TypedRequest>(data::class.java).serializerForRequest().serialize(data)).thenApply {
        val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(clazz)
        val deserializer = messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().first())
        deserializer.deserialize(it)
    }
