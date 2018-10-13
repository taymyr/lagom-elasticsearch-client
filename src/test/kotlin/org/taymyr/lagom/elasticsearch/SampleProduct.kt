package org.taymyr.lagom.elasticsearch

import org.taymyr.lagom.elasticsearch.document.dsl.Document
import org.taymyr.lagom.elasticsearch.search.dsl.AggregatedSearchResult
import org.taymyr.lagom.elasticsearch.search.dsl.Hits
import java.util.Date

data class SampleProduct(
    val id: Long,
    val category: Category,
    val fullTextBoosted: String,
    val fullText: String,
    val sellerId: Long,
    val categoryIds: List<Long>,
    val updateDate: Date,
    val basePrice: Int,
    val staticFacets: List<StaticFacets>,
    val payload: Payload
) {

    data class StaticFacets(
        val name: String,
        val value: String
    )

    data class Category(
        val id: Long,
        val title: String
    )

    data class Payload(
        val field: String
    )
}

data class IndexedSampleProduct(override val source: SampleProduct) : Document<SampleProduct>()

data class SampleProductResult(override val hits: Hits<SampleProduct>, override val aggregations: Any) : AggregatedSearchResult<SampleProduct, Any>()

data class SampleCategoryForProduct(val categoryId: Long, val categoryTitle: String)
