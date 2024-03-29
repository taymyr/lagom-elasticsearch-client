package org.taymyr.lagom.elasticsearch.search.dsl

import com.fasterxml.jackson.annotation.JsonProperty
import org.taymyr.lagom.elasticsearch.script.Script
import org.taymyr.lagom.elasticsearch.search.dsl.query.Query
import org.taymyr.lagom.elasticsearch.search.dsl.query.Sort
import org.taymyr.lagom.elasticsearch.search.dsl.query.aggregation.Aggregation
import org.taymyr.lagom.elasticsearch.search.dsl.query.highlight.Highlight
import org.taymyr.lagom.elasticsearch.search.dsl.query.script.ScriptField
import org.taymyr.lagom.elasticsearch.search.dsl.query.suggest.Suggest

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-body.html)
 *
 * @param aggs See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)
 */
data class SearchRequest @JvmOverloads constructor(
    val query: Query,
    @JsonProperty("script_fields")
    val scriptFields: List<ScriptField>? = null,
    val from: Int? = null,
    val size: Int? = null,
    val aggs: Map<String, Aggregation>? = null,
    @JsonProperty("post_filter")
    val postFilter: Query? = null,
    val suggest: Map<String, Suggest>? = null,
    val sort: List<Sort>? = null,
    @JsonProperty("min_score")
    val minScore: Double? = null,
    @JsonProperty("search_after")
    val searchAfter: List<Any>? = null,
    @JsonProperty("_source")
    val source: SourceFilter<*>? = null,
    val highlight: Highlight? = null
) {

    class Builder {
        private var query: Query? = null
        private var scriptFields: MutableList<ScriptField> = mutableListOf()
        private var from: Int? = null
        private var size: Int? = null
        private var aggs: MutableMap<String, Aggregation> = mutableMapOf()
        private var postFilter: Query? = null
        private var suggest: MutableMap<String, Suggest> = mutableMapOf()
        private var sort: MutableList<Sort> = mutableListOf()
        private var minScore: Double? = null
        private var searchAfter: MutableList<Any> = mutableListOf()
        private var source: SourceFilter<*>? = null
        private var highlight: Highlight? = null

        fun query(query: Query) = apply { this.query = query }
        fun scriptField(name: String, script: Script) = apply { this.scriptFields.add(ScriptField(name, script)) }
        fun from(from: Int) = apply { this.from = from }
        fun size(size: Int) = apply { this.size = size }
        fun aggs(aggs: Map<String, Aggregation>) = apply { this.aggs.putAll(aggs) }
        fun agg(name: String, aggregation: Aggregation) = apply { this.aggs[name] = aggregation }
        fun postFilter(postFilter: Query) = apply { this.postFilter = postFilter }
        fun suggest(suggest: Map<String, Suggest>) = apply { this.suggest.putAll(suggest) }
        fun suggest(name: String, suggest: Suggest) = apply { this.suggest[name] = suggest }
        fun sort(vararg sort: Sort) = apply { this.sort.addAll(sort) }
        fun sort(sort: List<Sort>) = apply { this.sort.addAll(sort) }
        fun minScore(minScore: Double) = apply { this.minScore = minScore }
        fun searchAfter(vararg searchAfter: Any) = apply { this.searchAfter.addAll(searchAfter) }
        fun searchAfter(searchAfter: List<Any>) = apply { this.searchAfter.addAll(searchAfter) }
        fun source(source: SourceFilter<*>) = apply { this.source = source }
        fun highlight(highlight: Highlight) = apply { this.highlight = highlight }

        fun build() = SearchRequest(
            query = query ?: error("Query can't be null"),
            scriptFields = if (scriptFields.isEmpty()) null else scriptFields.toList(),
            from = from,
            size = size,
            aggs = if (aggs.isEmpty()) null else aggs.toMap(),
            postFilter = postFilter,
            suggest = if (suggest.isEmpty()) null else suggest.toMap(),
            sort = if (sort.isEmpty()) null else sort.toList(),
            minScore = minScore,
            searchAfter = if (searchAfter.isEmpty()) null else searchAfter.toList(),
            source = source,
            highlight = highlight
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
