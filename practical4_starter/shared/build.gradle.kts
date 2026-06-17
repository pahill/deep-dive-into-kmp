import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {

    applyDefaultHierarchyTemplate()

    android {
        namespace = "com.jetbrains.cameraapp.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        androidResources { enable = true }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export("io.github.mirzemehdi:kmpnotifier:1.6.1")
        }
    }

    jvm()

    sourceSets {
        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.koin.compose.viewmodel)
                api(libs.permissions)
                api(libs.permissions.camera)
                api(libs.permissions.notifications)
                api(libs.permissions.gallery)
                implementation(libs.permissions.compose)
            }
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.material3)
            implementation(libs.material.icons.core)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.navigation3.ui)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
            api(libs.kmpnotifier)
        }

        jvmMain.dependencies {
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs.core)
            implementation(libs.filekit.dialogs.compose)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        androidMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.androidx.camera.core)
                implementation(libs.androidx.camera.camera2)
                implementation(libs.androidx.camera.lifecycle)
                implementation(libs.androidx.camera.view)
                implementation(libs.androidx.exifinterface)
            }
        }

        iosMain {
            dependsOn(mobileMain)
            dependencies {
                implementation(libs.filekit.core)
                implementation(libs.filekit.dialogs.core)
                implementation(libs.filekit.dialogs.compose)
            }
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.androidx.ui.tooling)
}