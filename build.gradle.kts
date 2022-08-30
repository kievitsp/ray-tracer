import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
}

group = "uk.co.kievits"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:multik-core:0.2.0")
    implementation("org.jetbrains.kotlinx:multik-default:0.2.0")

    testImplementation(kotlin("test"))
    testImplementation(platform("io.cucumber:cucumber-bom:7.6.0"))

    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-java8")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}