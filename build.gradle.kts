import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.allopen") version libs.versions.kotlin
    id("com.github.ben-manes.versions") version libs.versions.gradleVersions
    id("org.jetbrains.dokka") version libs.versions.dokka
    id("org.jetbrains.kotlinx.kover") version libs.versions.kover
    id("org.jetbrains.kotlinx.benchmark") version libs.versions.benchmark
    id("org.hibernate.build.maven-repo-auth") version libs.versions.mavenRepoAuth
    id("idea")
    `maven-publish`
}

val pocVersion: String by project

group = "de.potionlabs.poc"
version = pocVersion
java.sourceCompatibility = JavaVersion.VERSION_22
java.targetCompatibility = JavaVersion.VERSION_22
kotlin.sourceSets.all { languageSettings.languageVersion = KotlinVersion.KOTLIN_2_0.version }

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.kotlin.plugin.allopen")
        plugin("org.jetbrains.dokka")
        plugin("org.jetbrains.kotlinx.kover")
        plugin("org.hibernate.build.maven-repo-auth")
        plugin("org.jetbrains.kotlinx.benchmark")
        plugin("idea")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        testImplementation("org.jetbrains.kotlin:kotlin-test")
    }

    tasks.test {
        useJUnitPlatform()
    }

    sourceSets {
        create("benchmark")
    }

    kotlin {
        jvmToolchain(22)
        compilerOptions {
            apiVersion.set(KotlinVersion.KOTLIN_2_0)
            languageVersion.set(KotlinVersion.KOTLIN_2_0)
            jvmTarget.set(JvmTarget.JVM_22)
        }
        target {
            compilations.getByName("benchmark")
                .associateWith(compilations.getByName("main"))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_22.majorVersion))
        }
    }

    benchmark {
        targets {
            register("benchmark")
        }
    }

    allOpen {
        annotation("org.openjdk.jmh.annotations.State")
    }

    tasks.test {
        useJUnitPlatform()

        this.testLogging {
            this.showStandardStreams = true
        }
    }

    tasks.withType<Copy>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.named<KotlinCompilationTask<*>>("compileKotlin").configure {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            languageVersion.set(KotlinVersion.KOTLIN_2_0)
            apiVersion.set(KotlinVersion.KOTLIN_2_0)
        }
    }

    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)

        return isStable.not()
    }

    tasks.withType<DependencyUpdatesTask>().configureEach {
        gradleReleaseChannel = "current"
        revision = "release"

        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }

    /*
    kover {
        currentProject {
            sources {
                excludeJava = true
            }
        }
        //useKoverTool()
    }

    koverReport {
        filters {
            includes {
                classes("de.potionlabs.*")
                packages("de.potionlabs")
            }
        }

        defaults {
            xml {
                onCheck = false
                setReportFile(layout.buildDirectory.file("kover/result.xml"))
                filters {
                    includes {
                        classes("de.potionlabs.*")
                        packages("de.potionlabs")
                    }
                }
            }

            html {
                title = "Engine Coverage Report"
                onCheck = false
                setReportDir(layout.buildDirectory.dir("kover/html"))
                filters {
                    includes {
                        classes("de.potionlabs.*")
                        packages("de.potionlabs")
                    }
                }
            }

            verify {
                onCheck = true

                rule {
                    isEnabled = true
                    entity = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
                    filters {
                        includes {
                            classes("de.potionlabs.*")
                            packages("de.potionlabs")
                        }
                    }

                    bound {
                        minValue = 0
                        maxValue = 100
                        metric = kotlinx.kover.gradle.plugin.dsl.MetricType.LINE
                        aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                    }
                    minBound(0)
                    maxBound(100)
                }
            }
        }
    }
    */
}

repositories {
    mavenCentral()
    mavenLocal()
}