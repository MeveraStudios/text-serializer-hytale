plugins {
    signing
    `java-library`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

val baseVersion = "1.0.0"
val releaseSnapshots = true
val isSnapshot = System.getenv("SNAPSHOT_BUILD") == "true"

tasks.register("printReleaseSnapshots") {
    doLast { println("releaseSnapshots=$releaseSnapshots") }
}
tasks.register("printVersion") {
    doLast { println("baseVersion=$baseVersion") }
}

group = "studio.mevera"
version = if (isSnapshot && releaseSnapshots) "$baseVersion-SNAPSHOT" else baseVersion

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
    maven(url = "https://repo.gravemc.net/releases/")
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("com.hypixel:hytale-server:1.0.0")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}

signing {
    val key = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")?.replace("\\n", "\n")
    val password = System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")

    if (!key.isNullOrEmpty() && !password.isNullOrEmpty()) {
        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }
}

mavenPublishing {
    coordinates(group.toString(), project.name, version.toString())

    pom {
        name.set(project.name)
        description.set("A PaperMC/Adventure Component Serializer/Deserializer for Hytale Message Components.")
        inceptionYear.set("2026")
        url.set("https://github.com/MeveraStudios/text-serializer-hytale")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://mit-license.org/")
            }
        }

        developers {
            developer {
                id.set("iiahmedyt")
                name.set("iiAhmedYT")
                url.set("https://github.com/iiAhmedYT/")
            }
        }

        scm {
            url.set("https://github.com/MeveraStudios/text-serializer-hytale")
            connection.set("scm:git:git://github.com/MeveraStudios/text-serializer-hytale.git")
            developerConnection.set("scm:git:ssh://git@github.com:MeveraStudios/text-serializer-hytale.git")
        }
    }

    val isPublishToMavenLocal =
        gradle.startParameter.taskNames.any { it == "publishToMavenLocal" }

    if (!isPublishToMavenLocal && (!isSnapshot || releaseSnapshots)) {
        publishToMavenCentral(automaticRelease = true)
        signAllPublications()
    }
}
