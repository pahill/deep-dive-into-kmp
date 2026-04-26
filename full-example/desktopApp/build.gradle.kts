import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.filekit.dialogs.core)
                implementation(libs.filekit.dialogs.compose)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.jetbrains.cameraapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            appResourcesRootDir = layout.projectDirectory.dir("src/jvmMain/assets")
            packageName = "com.jetbrains.cameraapp"
            packageVersion = "1.0.0"
            jvmArgs += "-splash:${'$'}APPDIR/resources/splash.png"
        }
    }
}
