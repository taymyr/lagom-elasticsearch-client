package org.taymyr.lagom.elasticsearch.indices.dsl

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-tokenfilters.html).
 *
 * Subclass is selected by `type`. To deserialize, register it in a copy of `ElasticSerializerFactory.MAPPER`:
 * `MAPPER.copy().registerSubtypes(NamedType(MyFilter::class.java, "my_type"))`.
 *
 * Do not register subtypes directly in `ElasticSerializerFactory.MAPPER`:
 * the Lagom framework uses this at startup to create service serializers,
 * and the Jackson library caches deserializers, so subtypes registered later are not picked up.
 */
@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "type", visible = true)
abstract class Filter(open val type: String)
