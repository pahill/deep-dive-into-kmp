rootProject.name = "DeepDiveIntoKmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

includeBuild("full-example")
includeBuild("kover")
//includeBuild("lldb-example")
includeBuild("practical1_starter")
includeBuild("practical1_final")
includeBuild("practical2_starter")
includeBuild("practical3_starter")
includeBuild("practical3_final")
includeBuild("practical4_starter")
includeBuild("xcode-kotlin-example")
