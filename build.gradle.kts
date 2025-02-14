import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("io.quarkus")
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-vertx")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.vertx:vertx-jdbc-client")
    implementation("io.quarkus:quarkus-liquibase")
    implementation(libs.aws.advanced.jdbc.wrapper)

    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("io.quarkus:quarkus-messaging")
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-smallrye-fault-tolerance")
//    implementation("io.quarkus:quarkus-kubernetes-client")
    implementation("io.quarkus:quarkus-config-yaml")

    testImplementation(libs.assertj.core)
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("org.testcontainers:mysql")
    testImplementation(libs.awaitility)

    testImplementation("io.smallrye.reactive:smallrye-reactive-messaging-in-memory")
}

group = "org.acme"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")

    testLogging {
        events (TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true

        showStandardStreams = true
    }
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
