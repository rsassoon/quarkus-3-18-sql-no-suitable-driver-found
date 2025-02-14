rootProject.name="quarkus-3-18-sql-no-suitable-driver-found"

pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("aws-advanced-jdbc-wrapper", "software.amazon.jdbc", "aws-advanced-jdbc-wrapper").version("2.5.4")

            library("assertj-core", "org.assertj", "assertj-core").version("3.27.3")
            library("awaitility", "org.awaitility", "awaitility").version("4.2.2")
        }
    }
}