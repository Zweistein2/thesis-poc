rootProject.name = "thesis-poc"
include("ffmlibrary")
include("poc:steamworksFFM")
include("poc:steamworks4j")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("junit", "5.13.4")
            version("gson", "2.13.1")
            version("dokka", "2.1.0")
            version("ksp", "2.2.10-2.0.2")
            version("kspTest", "0.8.0")
            version("kotlin", "2.2.10")
            version("kotlinCoroutines", "1.10.2")
            version("kotlinPoet", "2.2.0")
            version("kover", "0.9.1")
            version("mockito", "5.19.0")
            version("hamcrest", "3.0")
            version("mavenRepoAuth", "3.0.4")
            version("gradleVersions", "0.52.0")
            version("benchmark", "0.4.16")
            version("steamworks4j", "1.10.0")

            library("kotlin.poet","com.squareup", "kotlinpoet-ksp").versionRef("kotlinPoet")
            library("kotlin.ksp","com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
            library("kotlinx.coroutines","org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinCoroutines")
            library("kotlinx.benchmark", "org.jetbrains.kotlinx", "kotlinx-benchmark-runtime").versionRef("benchmark")
            library("kotlin.stdlib","org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            library("kotlin.reflect","org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin.kspTestCore", "dev.zacsweers.kctfork", "core").versionRef("kspTest")
            library("kotlin.kspTestKsp", "dev.zacsweers.kctfork", "ksp").versionRef("kspTest")
            library("hamcrest","org.hamcrest", "hamcrest").versionRef("hamcrest")
            library("gson.gson","com.google.code.gson", "gson").versionRef("gson")
            library("junit.api","org.junit.jupiter", "junit-jupiter-api").versionRef("junit")
            library("junit.core","org.junit.jupiter", "junit-jupiter-engine").versionRef("junit")
            library("mockito.core","org.mockito", "mockito-core").versionRef("mockito")
            library("steamworks4j.core","com.code-disaster.steamworks4j", "steamworks4j").versionRef("steamworks4j")
            library("steamworks4j.lwjgl3","com.code-disaster.steamworks4j", "steamworks4j-gdx").versionRef("steamworks4j")

            bundle("junit", listOf("junit.api", "junit.core"))
            bundle("kotlin", listOf("kotlinx.coroutines", "kotlin.stdlib", "kotlin.reflect"))
            bundle("kspTest", listOf("kotlin.kspTestCore", "kotlin.kspTestKsp"))
            bundle("steamworks4j", listOf("steamworks4j.lwjgl3", "steamworks4j.core"))
        }
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}