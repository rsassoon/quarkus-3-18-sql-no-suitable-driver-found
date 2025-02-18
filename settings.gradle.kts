rootProject.name="quarkus-3-18-sql-no-suitable-driver-found"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // https://github.com/quarkusio/quarkus/releases
//            version("quarkus", "3.17.8") //works
            version("quarkus", "3.18.3") //3.18+ fails
            version("mockito", "5.15.2")
            version("pact", "4.6.16")

            plugin("quarkus", "io.quarkus").versionRef("quarkus")
            // https://github.com/diffplug/spotless
            plugin("spotless", "com.diffplug.spotless").version("7.0.2")
            // https://github.com/SonarSource/sonar-scanner-gradle
            plugin("sonarqube", "org.sonarqube").version("6.0.1.5171")
            // https://github.com/palantir/gradle-baseline
            plugin("errorprone", "com.palantir.baseline-error-prone").version("6.13.0")

            plugin("mutation.test", "info.solidsoft.pitest").version("1.15.0")

            library("bcpkix", "org.bouncycastle", "bcpkix-jdk18on").version("1.80")
            library("aws-advanced-jdbc-wrapper", "software.amazon.jdbc", "aws-advanced-jdbc-wrapper").version("2.5.4")
            library("quarkus-platform", "io.quarkus.platform", "quarkus-bom").versionRef("quarkus")
            library("quarkus-logging", "io.quarkus", "quarkus-logging-json").versionRef("quarkus")
            library("aws-msk-iam-auth", "software.amazon.msk", "aws-msk-iam-auth").version("2.2.0")


            library("unleash", "io.getunleash", "unleash-client-java").version("10.0.1")
            library("guava", "com.google.guava", "guava").version("33.4.0-jre")

            // testing
            library("awaitility", "org.awaitility", "awaitility").version("4.2.2")
            library("assertj-core", "org.assertj", "assertj-core").version("3.27.3")
            library("mockito-core", "org.mockito", "mockito-core").versionRef("mockito")
            library("mockito-junit", "org.mockito", "mockito-junit-jupiter").versionRef("mockito")
            library("pact-consumer-junit", "au.com.dius.pact.consumer", "junit5").versionRef("pact")
            library("pact-provider-junit", "au.com.dius.pact.provider", "junit5").versionRef("pact")
            library("jqwik", "net.jqwik", "jqwik").version("1.9.2")
            library("wiremock", "org.wiremock", "wiremock").version("3.11.0")
        }
    }
}