package org.taymyr.lagom.elasticsearch.search.dsl

import org.taymyr.lagom.elasticsearch.search.dsl.Relation.EQ

data class Total(val value: Int = 0, val relation: Relation = EQ)
