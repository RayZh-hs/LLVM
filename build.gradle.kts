import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create

plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
}

group = "space.norb"
version = "1.0.0"

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

// Generate sources and Javadoc jars, as required by Maven Central.
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier = "javadoc"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = uri(
                if (version.toString().endsWith("SNAPSHOT")) {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                }
            )
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_TOKEN")
            }
        }
    }

    // Define what artifacts to publish
    publications {
        create<MavenPublication>("mavenJava") {
            // Set the final coordinates for your library
            groupId = project.group.toString()
            artifactId = "llvm"
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name = "llvm"
                description = "Modern LLVM IR Generation Framework for Kotlin."
                url = "https://github.com/RayZh-hs/LLVM"

                licenses {
                    license {
                        name = "The MIT License"
                        url = "http://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "RayZh-hs"
                        name = "RayZh"
                        email = "ray_zh@sjtu.edu.cn"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/RayZh-hs/LLVM.git"
                    developerConnection = "scm:git:ssh://github.com/RayZh-hs/LLVM.git"
                    url = "https://github.com/RayZh-hs/LLVM.git"
                }
            }
        }
    }
}
