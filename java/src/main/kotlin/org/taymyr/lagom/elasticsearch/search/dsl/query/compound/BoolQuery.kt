package org.taymyr.lagom.elasticsearch.search.dsl.query.compound

import org.taymyr.lagom.elasticsearch.search.dsl.query.Query

/**
 * See [Elasticsearch Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-bool-query.html)
 */
data class BoolQuery(val bool: BoolQueryBody) : CompoundQuery {

    class Builder {
        private var should: MutableList<Query> = mutableListOf()
        private var mustNot: MutableList<Query> = mutableListOf()
        private var must: MutableList<Query> = mutableListOf()
        private var filter: MutableList<Query> = mutableListOf()

        fun should(should: Query) = apply { this.should.add(should) }
        fun should(should: List<Query>) = apply { this.should.addAll(should) }
        fun mustNot(mustNot: Query) = apply { this.mustNot.add(mustNot) }
        fun mustNot(mustNot: List<Query>) = apply { this.mustNot.addAll(mustNot) }
        fun must(must: Query) = apply { this.must.add(must) }
        fun must(must: List<Query>) = apply { this.must.addAll(must) }
        fun filter(filter: Query) = apply { this.filter.add(filter) }
        fun filter(filter: List<Query>) = apply { this.filter.addAll(filter) }

        fun build() = BoolQuery(
            BoolQueryBody(
                should = if (should.isEmpty()) null else should.toList(),
                mustNot = if (mustNot.isEmpty()) null else mustNot.toList(),
                must = if (must.isEmpty()) null else must.toList(),
                filter = if (filter.isEmpty()) null else filter.toList()
            )
        )
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}