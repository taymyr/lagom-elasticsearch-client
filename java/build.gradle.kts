import org.jetbrains.dokka.Platform.jvm
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.net.URL

val isReleaseVersion = !version.toString().endsWith("-SNAPSHOT")

object Versions {
    const val scalaBinary = "2.12" // "2.11" "2.13"
    const val lagom = "1.5.5" // "1.4.15" "1.6.2"
    const val ktlint = "0.43.2"
    const val `kotlin-logging` = "1.6.22"
    const val junit5 = "5.8.2"
    const val `json-unit` = "2.7.0"
    const val assertj = "3.12.2"
    const val jacoco = "0.8.2"
    const val jackson = "2.9.7"
    const val elasticsearch = "7.16.2"
    const val testcontainers = "1.18.3"
}

val lagomVersion = project.properties["lagomVersion"] as String? ?: Versions.lagom
val scalaBinaryVersion = project.properties["scalaBinaryVersion"] as String? ?: Versions.scalaBinary

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.dokka") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    `maven-publish`
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
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", Versions.jackson)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", Versions.jackson)
    compileOnly("com.lightbend.lagom", "lagom-javadsl-server_$scalaBinaryVersion", lagomVersion)

    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-params", Versions.junit5)
    testImplementation("org.junit.jupiter", "junit-jupiter-engine", Versions.junit5)
    testImplementation("org.assertj", "assertj-core", Versions.assertj)
    testImplementation("net.javacrumbs.json-unit", "json-unit-assertj", Versions.`json-unit`)
    testImplementation("com.lightbend.lagom", "lagom-javadsl-integration-client_$scalaBinaryVersion", lagomVersion)
    testImplementation("com.lightbend.lagom", "lagom-logback_$scalaBinaryVersion", lagomVersion)
    testImplementation("org.testcontainers", "testcontainers", Versions.testcontainers)
    testImplementation("org.testcontainers", "junit-jupiter", Versions.testcontainers)
    testImplementation("org.testcontainers", "elasticsearch", Versions.testcontainers)
}

ktlint {
    version.set(Versions.ktlint)
    outputToConsole.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
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
        xml.required.set(true)
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
    dokkaSourceSets {
        configureEach {
            jdkVersion.set(8)
            reportUndocumented.set(true)
            platform.set(jvm)
            externalDocumentationLink {
                url.set(URL("https://www.lagomframework.com/documentation/1.6.x/java/api/"))
            }
            displayName.set("JVM")
        }
    }
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
