import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.cjcrafter"
version = "0.1.0"

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    kotlin("jvm") version "1.7.20-RC"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

bukkit {
    main = "com.cjcrafter.mechanicseverywhere.MechanicsEverywhere"
    name = "MechanicsEverywhere"
    apiVersion = "1.13"

    authors = listOf("DeeCaaD", "CJCrafter")
    depend = listOf("MechanicsCore")
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/WeaponMechanics/MechanicsMain")
        credentials {
            username = findProperty("user").toString()
            password = findProperty("pass").toString()
        }
    }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")

    api("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("me.deecaad:mechanicscore:2.4.6")
    implementation("org.bstats:bstats-bukkit:3.0.1")
}

tasks.named<ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("MechanicsEverywhere-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    dependencies {
        relocate ("kotlin.", "com.cjcrafter.mechanicseverywhere.lib.kotlin.") {
            include(dependency("org.jetbrains.kotlin:"))
        }

        relocate("org.bstats", "com.cjcrafter.mechanicseverywhere.lib.bstats") {
            include(dependency("org.bstats:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(16)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

tasks.test {
    useJUnitPlatform()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "16"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "16"
}