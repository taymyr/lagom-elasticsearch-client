package org.taymyr.lagom.elasticsearch.dsl.mapping

class MappingProperties {
    companion object {

        @JvmStatic val LONG = MappingProperty(MappingTypes.LONG)
        @JvmStatic val TEXT = MappingProperty(MappingTypes.TEXT)
        @JvmStatic val OBJECT = MappingProperty(MappingTypes.OBJECT)
        @JvmStatic val BOOLEAN = MappingProperty(MappingTypes.BOOLEAN)
    }
}
