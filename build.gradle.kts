plugins {
    kotlin("jvm") version "1.9.23"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

group = "br.com.api.kaike"
version = "1.0.0"

application {
    mainClass.set("br.com.api.kaike.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Core e Servidor Netty
    implementation("io.ktor:ktor-server-core:2.3.11")
    implementation("io.ktor:ktor-server-netty:2.3.11")

    // Negociação de Conteúdo e Serialização JSON
    implementation("io.ktor:ktor-server-content-negotiation:2.3.11")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")

    // Páginas de Status para tratamento de erros
    implementation("io.ktor:ktor-server-status-pages:2.3.11")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.7")

    // Base de Dados
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}