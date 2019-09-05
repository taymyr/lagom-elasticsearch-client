val ossrhUsername: String? by project
val ossrhPassword: String? by project
val projectVersion: String by project

plugins {
    id("io.codearte.nexus-staging") version "0.21.1"
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {
    group = "org.taymyr.lagom"
    version = projectVersion
}

nexusStaging {
    packageGroup = "org.taymyr"
    username = ossrhUsername
    password = ossrhPassword
}