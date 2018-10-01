package org.taymyr.lagom.elasticsearch.test

import org.taymyr.lagom.elasticsearch.dsl.search.Hits
import org.taymyr.lagom.elasticsearch.dsl.search.SearchResult

class CategorySearchResult(
    hits: Hits<CategoryElastic>
) : SearchResult<CategoryElastic>(hits)
