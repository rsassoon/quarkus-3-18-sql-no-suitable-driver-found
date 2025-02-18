//import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    jacoco
    alias(libs.plugins.quarkus)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.spotless)
//    alias(libs.plugins.errorprone)
    alias(libs.plugins.mutation.test)
    id("com.adarshr.test-logger") version "4.0.0"
    id("au.com.dius.pact") version "4.6.16"
}

val changelogVersion = readVersionFromChangelog()

group = "org.acme"
version = System.getenv("CI_COMMIT_TAG") ?: System.getenv("CI_COMMIT_SHA") ?: changelogVersion

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}


repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(enforcedPlatform(libs.quarkus.platform))
    implementation("io.quarkus:quarkus-config-yaml")

    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-scheduler")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("io.quarkus:quarkus-messaging")
    implementation("io.quarkus:quarkus-smallrye-fault-tolerance")
    implementation("io.quarkus:quarkus-liquibase")

//    implementation("io.quarkus:quarkus-kubernetes-client")

    implementation(libs.bcpkix)
    implementation(libs.aws.msk.iam.auth)

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    implementation(libs.aws.advanced.jdbc.wrapper)
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.vertx:vertx-jdbc-client")

    //Feature Flag
    implementation(libs.unleash)

    implementation(libs.guava)

    // Observability
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-micrometer")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    implementation(libs.quarkus.logging)
    implementation("io.quarkus:quarkus-opentelemetry")

    //Testing
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-internal")
    testImplementation("io.quarkus:quarkus-test-kubernetes-client")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation(libs.assertj.core)
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.quarkus:quarkus-jacoco")
    testImplementation("io.smallrye.reactive:smallrye-reactive-messaging-in-memory")
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
    testImplementation(libs.wiremock)
    testImplementation(libs.pact.consumer.junit)
    testImplementation(libs.pact.provider.junit)
    testImplementation(libs.awaitility)
}

//https://docs.gradle.org/current/userguide/java_testing.html
tasks.test {
    // This will run all unit tests (i.e. all those without the tag 'integration')
    useJUnitPlatform {
        excludeTags("integration")
    }
    finalizedBy("integrationTest")
}

tasks.register<Test>("integrationTest") {
    // This will run only the tests tagged with 'integration'
    description = "Runs the integration tests."
    group = "Integration tests"
    val test by testing.suites.existing(JvmTestSuite::class)
    testClassesDirs = files(test.map { it.sources.output.classesDirs })
    classpath = files(test.map { it.sources.runtimeClasspath })
    useJUnitPlatform {
        includeTags("integration")
    }
    shouldRunAfter("test")
}
//
//tasks.check {
//    dependsOn("integrationTest")
//}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    testImplementation(libs.mockito.core)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.withType<Test> {
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR
        )
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true

        showStandardStreams = true
    }

    if (System.getenv("GITLAB_CI") != null) {
        println("Running QUARKUS with GITLAB_CI")
        systemProperty("quarkus.test.profile", "test-ci")

        // Just publish pacts if we are in CI pipeline
        systemProperty("pact.verifier.publishResults", true)
    }

    jvmArgs("-Xmx4g", "-javaagent:${mockitoAgent.asPath}")

    systemProperty("pactbroker.url", "http://test.broker/")

    java.testResultsDir = layout.buildDirectory.dir("test-results/test")
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    //https://quarkus.io/guides/tests-with-coverage#coverage-for-tests-not-using-quarkustest
    systemProperty("quarkus.jacoco.report", "false")
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    extensions.configure(JacocoTaskExtension::class) {
        excludeClassLoaders = listOf("*QuarkusClassLoader")
    }
}

tasks.jacocoTestReport {
    executionData(layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile)
    enabled = true
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

pact {
    publish {
        pactBrokerUrl = System.getenv("PACT_BROKER_URL")
    }
}

pitest {
    //https://github.com/hcoles/pitest/tags
    pitestVersion.set("1.17.2")
    junit5PluginVersion.set("1.2.1")
    timestampedReports.set(false)
    exportLineCoverage.set(true)
    testStrengthThreshold.set(90)
    mutationThreshold.set(90)
    threads.set(6)

    jvmArgs.add("-javaagent:${mockitoAgent.asPath}")

    if (System.getenv("GITLAB_CI") != null) {
        println("Running QUARKUS with GITLAB_CI")
        jvmArgs.add("-Dquarkus.test.profile=test-ci")
    }
}


spotless {
    java {
        targetExclude("**/build/")
        removeUnusedImports()
        palantirJavaFormat()
        formatAnnotations()
    }
}

// https://github.com/palantir/gradle-baseline/tree/develop?tab=readme-ov-file#compalantirbaseline-error-prone
tasks.withType<JavaCompile>().configureEach {
//    options.errorprone.disable("StrictUnusedVariable", "VarUsage", "SafeLoggingPropagation")
//    options.errorprone.disableWarningsInGeneratedCode = true
    options.compilerArgs.add("-Werror")
}

tasks.withType<JavaCompile> {
    finalizedBy(tasks.spotlessApply)
}

fun readVersionFromChangelog(): String {
    val regex = """## [^0-9]*(\d+\.\d+\.\d+) - \w+-\w+-\w+""".toRegex()

    val version = File("CHANGELOG.md").useLines { lines ->
        val (match) = lines
            .map {
                regex.find(it)
            }
            .filterNotNull()
            .first()
            .destructured
        match
    }

    println("Version from Changelog: $version")

    return version
}
