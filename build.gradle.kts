import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    java
    id("com.bnorm.power.kotlin-power-assert") version "0.12.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "uk.co.kievits"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
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
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {

    test {
        useJUnitPlatform()
        systemProperty("cucumber.junit-platform.naming-strategy", "long")
        jvmArgs("--add-modules=jdk.incubator.vector")
    }

    withType<JavaCompile> {
        options.release.set(17)
        options.compilerArgs.add("--add-modules=jdk.incubator.vector")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
