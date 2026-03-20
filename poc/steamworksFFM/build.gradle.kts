plugins {
    id("com.google.devtools.ksp") version libs.versions.ksp
}

dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.gson.gson)
    implementation(project(":ffmlibrary"))
    ksp(project(":ffmlibrary") )
    testImplementation(libs.junit.api)
    testImplementation(libs.mockito.core)
    testRuntimeOnly(libs.junit.core)
    benchmarkImplementation(libs.kotlinx.benchmark)
}

sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}