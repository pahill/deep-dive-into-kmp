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

    androidLibrary {
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
            export("io.github.mirzemehdi:kmpnotifier:1.5.1")
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
                api("dev.icerock.moko:permissions-camera:0.20.1")
                api("dev.icerock.moko:permissions-notifications:0.20.1")
                api("dev.icerock.moko:permissions-gallery:0.20.1")
                implementation(libs.permissions.compose)
            }
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(libs.material.icons.core)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.compose)
            api("io.github.mirzemehdi:kmpnotifier:1.5.1")
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