plugins {
  kotlin("jvm") version "2.2.0"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  application
}

application {
  mainClass.set("org.example.MainKt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
  testImplementation(kotlin("test"))
}

tasks.jar {
  archiveClassifier.set("")
  manifest {
    attributes["Main-Class"] = "org.example.MainKt"
  }
}

tasks.test {
  useJUnitPlatform()
}
kotlin {
  jvmToolchain(21)
}
