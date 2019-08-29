import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.net.URL

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

val ossrhUsername: String? by project
val ossrhPassword: String? by project

object Versions {
    const val scalaBinary = "2.12"
    const val lagom = "1.5.1" // "1.4.15" "1.6.0-M5"
    const val ktlint = "0.31.0"
    const val `kotlin-logging` = "1.6.22"
    const val junit5 = "5.3.2"
    const val `json-unit` = "2.7.0"
    const val assertj = "3.12.2"
    const val jacoco = "0.8.2"
    const val jackson = "2.9.7"
    const val elasticsearch = "6.4.1"
    const val `embedded-elasticsearch` = "2.10.0"
}

val lagomVersion = project.properties["lagomVersion"] as String? ?: Versions.lagom
val scalaBinaryVersion = project.properties["scalaBinaryVersion"] as String? ?: Versions.scalaBinary

plugins {
    kotlin("jvm") version "1.3.50"
    id("org.jetbrains.dokka") version "0.9.18"
    id("org.jlleitschuh.gradle.ktlint") version "8.1.0"
    id("de.marcphilipp.nexus-publish") version "0.3.1"
    signing
    jacoco
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"
compileKotlin.kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"
compileTestKotlin.kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"

val compileTestJava: JavaCompile by tasks
compileTestJava.options.compilerArgs.add("-parameters")

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(kotlin("reflect"))
    compile("com.fasterxml.jackson.module", "jackson-module-kotlin", Versions.jackson)
    compile("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", Versions.jackson)
    compileOnly("com.lightbend.lagom", "lagom-javadsl-server_$scalaBinaryVersion", lagomVersion)

    testCompile("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testCompile("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testCompile("org.assertj", "assertj-core", Versions.assertj)
    testCompile("net.javacrumbs.json-unit", "json-unit-assertj", Versions.`json-unit`)
    testCompile("com.lightbend.lagom", "lagom-javadsl-integration-client_$scalaBinaryVersion", lagomVersion)
    testCompile("com.lightbend.lagom", "lagom-logback_$scalaBinaryVersion", lagomVersion)
    testCompile("pl.allegro.tech", "embedded-elasticsearch", Versions.`embedded-elasticsearch`)
}

ktlint {
    version.set(Versions.ktlint)
    outputToConsole.set(true)
    reporters.set(setOf(ReporterType.CHECKSTYLE))
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy("jacocoTestReport")
}

jacoco {
    toolVersion = Versions.jacoco
}
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokka)
}

tasks.dokka {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
    jdkVersion = 8
    reportUndocumented = true
    impliedPlatforms = mutableListOf("JVM")
    externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
        url = URL("https://www.lagomframework.com/documentation/1.4.x/java/api/")
    })
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "${project.name}_$scalaBinaryVersion"
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            pom {
                name.set("Taymyr: Lagom Elasticsearch Client")
                description.set("Lagom Elasticsearch client")
                url.set("https://taymyr.org")
                organization {
                    name.set("Digital Economy League")
                    url.set("https://www.digitalleague.ru/")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("taymyr")
                        name.set("Taymyr Contributors")
                        email.set("contributors@taymyr.org")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/taymyr/lagom-elasticsearch-client.git")
                    developerConnection.set("scm:git:https://github.com/taymyr/lagom-elasticsearch-client.git")
                    url.set("https://github.com/taymyr/lagom-elasticsearch-client")
                    tag.set("HEAD")
                }
            }
        }
    }
}

signing {
    isRequired = isReleaseVersion
    sign(publishing.publications["maven"])
}

nexusPublishing {
    repositories {
        sonatype()
    }
}