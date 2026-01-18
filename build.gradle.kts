plugins {
    id("java")
}

group = "studio.mevera"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.gravemc.net/releases/")
    }
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.26.1")
    compileOnly("com.hypixel:hytale-server:1.0.0")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
}