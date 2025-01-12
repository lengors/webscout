/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 * For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.6/userguide/multi_project_builds.html in the Gradle documentation.
 */

pluginManagement {
    val ktlintPluginVersion: String by settings
    val sonarqubeVersion: String by settings
    val kotlinVersion: String by settings
    val dokkaVersion: String by settings
    val koverVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.sonarqube") version sonarqubeVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
    }
}

rootProject.name = "webscout"