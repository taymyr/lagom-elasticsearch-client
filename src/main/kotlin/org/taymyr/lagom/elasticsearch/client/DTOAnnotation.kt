package org.taymyr.lagom.elasticsearch.client

/**
 * Need to mark data classes by this annotation for creating NoArg constructors for this data classes
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DTOAnnotation
