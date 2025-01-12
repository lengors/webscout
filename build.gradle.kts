plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

group = "io.github.lengors"

val ktlintVersion: String by properties
val javaVersion: String by properties

java.toolchain {
    languageVersion = JavaLanguageVersion.of(javaVersion)
}

configurations.compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
}

repositories {
    mavenCentral()
}

kotlin.compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
}

ktlint {
    version = ktlintVersion
}
