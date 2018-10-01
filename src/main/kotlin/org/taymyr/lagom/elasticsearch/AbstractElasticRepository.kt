package org.taymyr.lagom.elasticsearch

import akka.Done
import akka.NotUsed
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import org.taymyr.lagom.elasticsearch.dsl.UpdateIndexItem
import org.taymyr.lagom.elasticsearch.dsl.search.QueryRoot
import org.taymyr.lagom.elasticsearch.dsl.search.SearchResult
import org.taymyr.lagom.elasticsearch.serialize.JsonBytes
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

abstract class AbstractElasticRepository<IT : org.taymyr.lagom.elasticsearch.Indexable, QRT : SearchResult<IT>>(
    private val elasticSearch: org.taymyr.lagom.elasticsearch.ElasticSearch
) {

    private val mapper = ObjectMapper()
    private val queryResultTypeRef: TypeReference<QRT>

    init {
        val typeArgs = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        this.queryResultTypeRef = object : TypeReference<QRT>() {
            override fun getType() = typeArgs[1]
        }
        mapper.registerModule(Jdk8Module())
    }

    protected abstract fun getIndexName(): String

    protected abstract fun getTypeName(): String

    fun store(document: Optional<IT>): CompletionStage<Done> = document
        .map { doc -> elasticSearch.updateIndex(getIndexName(), getTypeName(), doc.getId())
            .invoke(convert(UpdateIndexItem(doc))) }
        .orElse(CompletableFuture.completedFuture(Done.getInstance()))

    fun search(query: QueryRoot): CompletionStage<QRT> = elasticSearch.search(getIndexName(), getTypeName())
        .invoke(query).thenApply { jB ->
            try {
                val reader = mapper.readerFor(queryResultTypeRef)
                return@thenApply reader.readValue(jB.bytes) as QRT
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

    fun delete(document: Optional<IT>): CompletionStage<Done> = document
        .map { i -> elasticSearch.deleteById(getIndexName(), getTypeName(), i.getId())
            .invoke(NotUsed.notUsed()) }
        .orElse(CompletableFuture.completedFuture(Done.getInstance()))

    private fun convert(request: UpdateIndexItem<IT>): JsonBytes {
        try {
            return JsonBytes(mapper.writeValueAsBytes(request))
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
