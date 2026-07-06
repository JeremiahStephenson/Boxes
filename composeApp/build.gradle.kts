import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xexpect-actual-classes",
            "-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.jerry.shapes.util.Parcelize",
            "-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.jerry.shapes.util.IgnoredOnParcel",
        )
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-jvm-default=enable",
            )
        }
    }

    jvm("desktop")

    // wasmJs {
    //     browser {
    //         commonWebpackConfig {
    //             outputFileName = "composeApp.js"
    //         }
    //     }
    //     binaries.executable()
    // }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain = getByName("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${libs.versions.lifecycle.get()}")
                implementation(libs.koin.core)
                implementation("io.insert-koin:koin-compose:${libs.versions.koinCompose.get()}")
                implementation("io.insert-koin:koin-compose-viewmodel:${libs.versions.koinCompose.get()}")

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlin.io)
                
                implementation(libs.androidx.room.runtime)
                implementation(libs.reorderable)
                implementation(libs.coil.compose)
            }
        }

        val androidMain = getByName("androidMain") {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.room.runtime)
                implementation(libs.timber)
                
                implementation(libs.firebase.analytics)
                implementation(libs.firebase.crashlytics)
                implementation(libs.androidx.datastore.preferences)

                // Navigation 3
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.navigation3.runtime)
                implementation(libs.androidx.lifecycle.viewmodel.navigation3)
                implementation(libs.compose.color.picker)
            }
        }

        val desktopMain = getByName("desktopMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val iosMain = create("iosMain") {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.room.runtime)
            }
        }
        
        getByName("iosArm64Main") { dependsOn(iosMain) }
        getByName("iosSimulatorArm64Main") { dependsOn(iosMain) }
    }
}

android {
    namespace = "com.jerry.shapes"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.jerry.shapes"
        minSdk = 24
        targetSdk = 37
        versionCode = 3
        versionName = "1.02"
    }

    signingConfigs {
        create("release") {
            keyAlias = "RouteSucks"
            keyPassword = "RouteSucks"
            storeFile = file("../../key.jks")
            storePassword = "RouteSucks"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    "androidMainImplementation"(platform(libs.firebase.bom))
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.jerry.shapes"
            packageVersion = "1.0.0"
        }
    }
}
