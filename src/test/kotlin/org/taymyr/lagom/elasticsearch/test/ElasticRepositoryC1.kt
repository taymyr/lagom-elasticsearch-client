package org.taymyr.lagom.elasticsearch.test

import org.taymyr.lagom.elasticsearch.AbstractElasticRepository
import org.taymyr.lagom.elasticsearch.ElasticSearch

class ElasticRepositoryC1(
    elasticSearch: ElasticSearch
) : AbstractElasticRepository<CategoryElastic, CategorySearchResult>(elasticSearch) {

    override fun getIndexName() = "c1"

    override fun getTypeName() = "all"
}
