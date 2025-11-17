import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.registering

plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
    signing

    id("com.gradleup.nmcp.aggregation") version "1.2.1"
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
        if (!project.hasProperty("example")) {
            throw GradleException("Please specify which example to run using -Pexample=<ExampleName>. Example: ./gradlew run -Pexample=AbsExample")
        }

        val exampleName = project.property("example") as String
        mainClass.set("space.norb.llvm.examples.${exampleName}Kt")
    }
}

// Generate Javadoc & Sources Jars
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "llvm"
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())

            pom {
                name.set("llvm")
                description.set("Modern LLVM IR Generation Framework for Kotlin.")
                url.set("https://github.com/RayZh-hs/LLVM")

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("http://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("RayZh-hs")
                        name.set("RayZh")
                        email.set("ray_zh@sjtu.edu.cn")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/RayZh-hs/LLVM.git")
                    developerConnection.set("scm:git:ssh://github.com/RayZh-hs/LLVM.git")
                    url.set("https://github.com/RayZh-hs/LLVM.git")
                }
            }
        }
    }
}

// Signing
signing {
    val secretKey = System.getenv("OSSRH_GPG_SECRET_KEY")
    val password = System.getenv("OSSRH_GPG_SECRET_KEY_PASSWORD")

    if (!secretKey.isNullOrBlank() && !password.isNullOrBlank()) {
        useInMemoryPgpKeys(secretKey, password)
        sign(publishing.publications["mavenJava"])
    } else {
        logger.warn("PGP signing is not configured. Skipping signing.")
    }
}

// Configure upload to Maven Central Portal
nmcpAggregation {
    centralPortal {
        username = System.getenv("MAVEN_CENTRAL_USERNAME")
        password = System.getenv("MAVEN_CENTRAL_PASSWORD")
        publishingType = "AUTOMATIC"
    }

    publishAllProjectsProbablyBreakingProjectIsolation()
}