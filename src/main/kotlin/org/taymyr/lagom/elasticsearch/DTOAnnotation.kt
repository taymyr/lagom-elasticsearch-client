package org.taymyr.lagom.elasticsearch

/**
 * Need to mark data classes by this annotation for creating NoArg constructors for this data classes
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DTOAnnotation
