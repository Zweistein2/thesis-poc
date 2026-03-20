dependencies {
    implementation(libs.bundles.kotlin)
    implementation(libs.kotlin.ksp)
    implementation(libs.kotlin.poet)
    implementation(libs.gson.gson)
    testImplementation(libs.bundles.kspTest)
    testImplementation(libs.junit.api)
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.core)
    testRuntimeOnly(libs.junit.core)
}

val ffmlibraryVersion: String by project

group = "de.potionlabs.ffmlibrary"
version = ffmlibraryVersion