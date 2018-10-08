package org.taymyr.lagom.elasticsearch.indices.dsl

/**
 * @author Ilya Korshunov
 */
class MappingProperties {
    companion object {
        @JvmStatic val LONG = MappingProperty(MappingTypes.LONG)
        @JvmStatic val TEXT = MappingProperty(MappingTypes.TEXT)
        @JvmStatic val OBJECT = MappingProperty(MappingTypes.OBJECT)
        @JvmStatic val BOOLEAN = MappingProperty(MappingTypes.BOOLEAN)
    }
}