@file:JvmName("ServiceCall")
package org.taymyr.lagom.elasticsearch

import akka.NotUsed
import akka.util.ByteString
import com.lightbend.lagom.javadsl.api.ServiceCall
import org.taymyr.lagom.elasticsearch.deser.ElasticSerializerFactory
import java.util.concurrent.CompletionStage

var elasticSerializerFactory = ElasticSerializerFactory()

fun <TypedRequest : Any> serializeRequest(data: TypedRequest, clazz: Class<out TypedRequest>): ByteString =
    elasticSerializerFactory.messageSerializerFor<TypedRequest>(clazz).serializerForRequest().serialize(data)

fun <TypedResponse : Any> deserializeResponse(clazz: Class<TypedResponse>): (ByteString) -> TypedResponse = {
    val messageSerializer = elasticSerializerFactory.messageSerializerFor<TypedResponse>(clazz)
    messageSerializer.deserializer(messageSerializer.acceptResponseProtocols().getOrNull(0)).deserialize(it)
}

fun <TypedRequest : Any, Response : Any> ServiceCall<ByteString, Response>.invoke(data: TypedRequest): CompletionStage<Response> =
    invoke(serializeRequest(data, data::class.java))

fun <TypedResponse : Any> ServiceCall<NotUsed, ByteString>.invoke(clazz: Class<TypedResponse>): CompletionStage<TypedResponse> =
    invoke().thenApply(deserializeResponse(clazz))

fun <Request : Any, TypedResponse : Any> ServiceCall<Request, ByteString>.invoke(
    data: Request,
    clazz: Class<TypedResponse>
): CompletionStage<TypedResponse> =
    invoke(data).thenApply(deserializeResponse(clazz))

fun <TypedRequest : Any, TypedResponse : Any> ServiceCall<ByteString, ByteString>.invoke(
    data: TypedRequest,
    clazzRequest: Class<TypedRequest>,
    clazzResponse: Class<TypedResponse>
): CompletionStage<TypedResponse> =
    invoke(
        serializeRequest(
            data,
            clazzRequest
        )
    ).thenApply(deserializeResponse(clazzResponse))

inline fun <reified TypedResponse : Any> ServiceCall<NotUsed, ByteString>.typedInvoke(): CompletionStage<TypedResponse> =
    typedInvoke(NotUsed.getInstance())

inline fun <reified Request : Any, reified TypedRequest : Any, reified TypedResponse : Any> ServiceCall<Request, ByteString>.typedInvoke(
    data: TypedRequest
): CompletionStage<TypedResponse> =
    when (data) {
        is Request -> invoke(data)
        else -> invoke(serializeRequest(data, TypedRequest::class.java) as Request)
    }.thenApply(deserializeResponse(TypedResponse::class.java))
