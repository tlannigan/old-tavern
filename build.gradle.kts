import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion = "1.7.0"

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.tlannigan"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.md-5.net/content/groups/public/")
}

dependencies {
    implementation("org.litote.kmongo:kmongo-serialization:4.6.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("dev.jorel:commandapi-shade:8.4.1")
    implementation("LibsDisguises:LibsDisguises:10.0.28")

    testImplementation(kotlin("test"))

    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-core:8.4.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar> {
    archiveFileName.set("${project.name}-${project.version}.jar")
    relocate("dev.jorel.commandapi", "com.tlannigan.tavern.commandapi")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}