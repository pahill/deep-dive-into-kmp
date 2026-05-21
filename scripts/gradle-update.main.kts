#!/usr/bin/env kotlin

import java.io.File
import java.util.Properties
import kotlin.system.exitProcess

val rootDir = File(".").canonicalFile
val configFile = rootDir.resolve("gradle_configuration.properties")

require(configFile.isFile) {
    "Missing config file: ${configFile.relativeTo(rootDir)}"
}

require(args.size <= 1) {
    "Usage: kotlin scripts/gradle-update.main.kts [project-folder]"
}

val requestedProjectDir = args.singleOrNull()
    ?.let { rootDir.resolve(it).canonicalFile }

if (requestedProjectDir != null) {
    require(requestedProjectDir.isDirectory) {
        "Project folder does not exist or is not a directory: ${requestedProjectDir.relativeToOrSelf(rootDir)}"
    }

    require(requestedProjectDir.toPath().startsWith(rootDir.toPath())) {
        "Project folder must be inside repository root: $requestedProjectDir"
    }
}

val properties = Properties().apply {
    configFile.inputStream().use(::load)
}

fun requiredProperty(name: String): String {
    return requireNotNull(properties.getProperty(name)?.trim()?.takeIf { it.isNotEmpty() }) {
        "Missing required property: $name"
    }
}

fun optionalProperty(name: String, defaultValue: String): String {
    return properties.getProperty(name)?.trim()?.takeIf { it.isNotEmpty() } ?: defaultValue
}

val gradleVersion = requiredProperty("gradleVersion")

val distributionType = optionalProperty("distributionType", "bin")
require(distributionType == "bin" || distributionType == "all") {
    "distributionType must be either 'bin' or 'all', but was '$distributionType'"
}

val toolchainVersion = requiredProperty("toolchainVersion")
val toolchainVendor = requiredProperty("toolchainVendor")

val ignoredDirectoryNames = setOf(
    ".git",
    ".gradle",
    "build",
    ".idea",
    ".kotlin",
    "node_modules",
    "DerivedData"
)

fun isGradleProject(directory: File): Boolean {
    val hasSettingsFile =
        directory.resolve("settings.gradle").isFile ||
                directory.resolve("settings.gradle.kts").isFile

    val hasBuildFile =
        directory.resolve("build.gradle").isFile ||
                directory.resolve("build.gradle.kts").isFile

    return hasSettingsFile && hasBuildFile
}

fun hasGradleWrapper(directory: File): Boolean {
    return directory.resolve("gradlew").isFile &&
            directory.resolve("gradlew.bat").isFile
}

fun findGradleProjects(directory: File): List<File> {
    val result = mutableListOf<File>()

    fun walk(current: File) {
        if (!current.isDirectory) return
        if (current.name in ignoredDirectoryNames) return

        if (isGradleProject(current)) {
            result += current
        }

        current.listFiles()
            ?.asSequence()
            ?.filter { it.isDirectory }
            ?.sortedBy { it.name }
            ?.forEach(::walk)
    }

    walk(directory)

    return result
        .distinctBy { it.canonicalPath }
        .sortedBy { it.relativeTo(rootDir).path }
}

fun runCommand(
    workingDir: File,
    command: List<String>
): Int {
    println()
    println("→ ${workingDir.relativeTo(rootDir)}")
    println("  ${command.joinToString(" ")}")

    val process = ProcessBuilder(command)
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()

    process.inputStream.bufferedReader().useLines { lines ->
        lines.forEach(::println)
    }

    return process.waitFor()
}

fun gradleCommandFor(projectDir: File): List<String> {
    val unixWrapper = projectDir.resolve("gradlew")
    val windowsWrapper = projectDir.resolve("gradlew.bat")
    val osName = System.getProperty("os.name").lowercase()

    return when {
        unixWrapper.isFile -> {
            if (!unixWrapper.canExecute()) {
                unixWrapper.setExecutable(true)
            }

            listOf(unixWrapper.absolutePath)
        }

        windowsWrapper.isFile && osName.contains("windows") -> {
            listOf(windowsWrapper.absolutePath)
        }

        windowsWrapper.isFile -> {
            listOf("cmd", "/c", windowsWrapper.absolutePath)
        }

        else -> error("Project has no Gradle wrapper: ${projectDir.relativeTo(rootDir)}")
    }
}

val projects = if (requestedProjectDir != null) {
    require(isGradleProject(requestedProjectDir)) {
        "Requested folder is not a Gradle project: ${requestedProjectDir.relativeTo(rootDir)}"
    }

    require(hasGradleWrapper(requestedProjectDir)) {
        "Requested Gradle project does not have wrapper files: ${requestedProjectDir.relativeTo(rootDir)}"
    }

    listOf(requestedProjectDir)
} else {
    findGradleProjects(rootDir)
        .filter(::hasGradleWrapper)
}

if (projects.isEmpty()) {
    println("No Gradle projects with wrappers found.")
    exitProcess(0)
}

println("Root: $rootDir")
if (requestedProjectDir != null) {
    println("Requested project: ${requestedProjectDir.relativeTo(rootDir)}")
}
println("Gradle version: $gradleVersion")
println("Distribution type: $distributionType")
println("Toolchain version: $toolchainVersion")
println("Toolchain vendor: $toolchainVendor")

println()
println("Found ${projects.size} Gradle project(s) with wrappers:")
projects.forEach {
    println(" - ${it.relativeTo(rootDir)}")
}

var failures = 0

projects.forEach { projectDir ->
    val gradleCommand = gradleCommandFor(projectDir)

    val wrapperVersionCommand = gradleCommand + listOf(
        "--no-daemon",
        "wrapper",
        "--gradle-version",
        gradleVersion,
        "--distribution-type",
        distributionType
    )

    val wrapperVersionExitCode = runCommand(projectDir, wrapperVersionCommand)

    if (wrapperVersionExitCode != 0) {
        failures += 1
        println("Failed to update wrapper version: ${projectDir.relativeTo(rootDir)}")

        return@forEach
    }

    val wrapperRefreshCommand = gradleCommand + listOf(
        "--no-daemon",
        "wrapper"
    )

    val wrapperRefreshExitCode = runCommand(projectDir, wrapperRefreshCommand)

    if (wrapperRefreshExitCode != 0) {
        failures += 1
        println("Failed to refresh wrapper files: ${projectDir.relativeTo(rootDir)}")

        return@forEach
    }

    val daemonJvmCommand = gradleCommand + listOf(
        "--no-daemon",
        "updateDaemonJvm",
        "--jvm-version",
        toolchainVersion,
        "--jvm-vendor",
        toolchainVendor
    )

    val daemonJvmExitCode = runCommand(projectDir, daemonJvmCommand)

    if (daemonJvmExitCode != 0) {
        failures += 1
        println("Failed to update daemon JVM criteria: ${projectDir.relativeTo(rootDir)}")
    }
}

println()
if (failures == 0) {
    println("Done. Updated ${projects.size} Gradle project(s).")
} else {
    println("Done with $failures failure(s).")
    exitProcess(1)
}