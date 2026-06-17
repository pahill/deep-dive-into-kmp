import org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)

}

kotlin {
    @OptIn(ExperimentalSwiftExportDsl::class)
    swiftExport {
        // Root module name
        moduleName = "Shared"

        // Collapse rule
        flattenPackage = "com.jetbrains.whereiskodee.whereiskodee"
    }


    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

