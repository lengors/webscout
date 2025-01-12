plugins {
    kotlin("jvm")
}

group = "io.github.lengors"

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
