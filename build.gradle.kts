import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import java.net.URI
import java.nio.file.Paths

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
}

buildscript {
    val dokkaVersioningPluginVersion: String by properties
    dependencies {
        classpath("org.jetbrains.dokka:versioning-plugin:$dokkaVersioningPluginVersion")
    }
}

group = "io.github.lengors"

val dokkaVersioningPluginVersion: String by properties
val jacksonBuildScriptVersion: String by properties
val ktlintVersion: String by properties
val javaVersion: String by properties

java.toolchain {
    languageVersion = JavaLanguageVersion.of(javaVersion)
}

configurations {
    // TODO temporary fix (explained: https://github.com/Kotlin/dokka/issues/3472)
    matching { it.name.startsWith("dokka") }
        .configureEach {
            resolutionStrategy.eachDependency {
                if (requested.group.startsWith("com.fasterxml.jackson")) {
                    useVersion(jacksonBuildScriptVersion)
                }
            }
        }

    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:versioning-plugin:$dokkaVersioningPluginVersion")
}

kotlin.compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
}

ktlint {
    version = ktlintVersion
}

tasks.withType<DokkaGeneratePublicationTask> {
    val docsDir =
        project
            .findProperty("dokkaOutputDir")
            ?.toString()
            ?.let(Paths::get)
            ?.toFile()
            ?: projectDir
                .resolve("build")
                .resolve("dokka")
                .resolve("generated")
    val currentVersion = "${project.version}"
    val versionsDir =
        project
            .findProperty("dokkaVersionsDir")
            ?.toString()
            ?.let(Paths::get)
            ?.toFile()
    doFirst {
        versionsDir?.let {
            docsDir
                .resolve("older")
                .takeIf { olderDir -> olderDir.isDirectory }
                ?.renameTo(it)
            docsDir
                .resolve("version.json")
                .takeIf { versionFile -> versionFile.isFile }
                ?.also { versionFile ->
                    jacksonObjectMapper()
                        .readTree(versionFile)
                        .get("version")
                        .textValue()
                        ?.let { version -> docsDir.renameTo(it.resolve(version)) }
                }
        }
    }
    outputDirectory = docsDir
    generator.dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = projectDir.resolve("src")
            remoteUrl = URI.create("https://github.com/lengors/${project.name}/tree/$currentVersion/src")
            remoteLineSuffix = "#L"
        }
    }
    generator.pluginsConfiguration.versioning {
        olderVersionsDir = versionsDir
        version = currentVersion
    }
    generator.pluginsConfiguration.html {
        customAssets.from(file("dokka/assets/github.svg"))
        customStyleSheets.from(file("dokka/styleSheets/custom.css"))
        templatesDir = file("dokka/templates")
    }
}
