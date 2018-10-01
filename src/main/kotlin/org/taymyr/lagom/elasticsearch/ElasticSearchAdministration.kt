package org.taymyr.lagom.elasticsearch

import akka.Done
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.taymyr.lagom.elasticsearch.dsl.mapping.ElasticSearchMapping
import org.taymyr.lagom.elasticsearch.dsl.settings.ElasticSearchIndexSettings
import org.taymyr.lagom.elasticsearch.serialize.JsonBytes
import java.io.File
import java.util.concurrent.CompletionStage

class ElasticSearchAdministration(
    private val elasticSearch: ElasticSearch,
    private val indexName: String,
    private val typeName: String
) {

    private val mapper = ObjectMapper()
    private val settingsMapper = ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE)

    fun createIndexWithSettings(settings: String): CompletionStage<Done> =
        elasticSearch.createIndexWithSettings(indexName)
            .invoke(JsonBytes(settings.toByteArray()))

    fun createIndexWithSettings(settings: ElasticSearchIndexSettings): CompletionStage<Done> =
        this.createIndexWithSettings(settingsMapper.writeValueAsString(settings))

    fun deleteIndex(): CompletionStage<Done> = elasticSearch.deleteIndex(indexName).invoke()

    fun createMapping(mapping: String): CompletionStage<Done> =
        elasticSearch.createMapping(indexName, typeName).invoke(JsonBytes(mapping.toByteArray()))

    fun createMapping(mapping: Map<String, ElasticSearchMapping>): CompletionStage<Done> =
        this.createMapping(mapper.writeValueAsString(mapping))

    fun initializeSettings(): CompletionStage<Done> {
        val uri = this::class.java.classLoader.getResource("elasticsearch/$indexName/settings.json")
        val json = uri.readText()
        return createIndexWithSettings(json)
    }

    fun initializeSettings(path: String): CompletionStage<Done> {
        val json = File(path).readText()
        return createIndexWithSettings(json)
    }

    fun initializeMapping(): CompletionStage<Done> {
        val uri = this::class.java.classLoader
            .getResource("elasticsearch/$indexName/${typeName}_mapping.json")
        val json = uri.readText()
        return createMapping(json)
    }

    fun initializeMapping(path: String): CompletionStage<Done> {
        val json = File(path).readText()
        return createMapping(json)
    }
}
