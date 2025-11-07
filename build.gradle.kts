plugins {
    kotlin("jvm") version "2.2.0"
}

group = "space.norb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.2.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

// Register custom tasks to run examples
tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs a specified Kotlin JVM example. Usage: ./gradlew run -Pexample=<ExampleName>"
    classpath = sourceSets["main"].runtimeClasspath

    if ("run" in gradle.startParameter.taskNames) {
        // Check for the project property at configuration time.
        if (!project.hasProperty("example")) {
            throw GradleException("Please specify which example to run using -Pexample=<ExampleName>. Example: ./gradlew run -Pexample=AbsExample")
        }

        // Set the main class dynamically based on the property.
        val exampleName = project.property("example") as String
        mainClass.set("space.norb.llvm.examples.${exampleName}Kt")
    }
}
