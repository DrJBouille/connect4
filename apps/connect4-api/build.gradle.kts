import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "2.2.10"
  kotlin("plugin.allopen") version "2.2.10"
  id("io.quarkus")
}

repositories {
  mavenCentral()
  mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
  implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-rest-jackson")
  implementation("io.quarkus:quarkus-jackson")
  implementation("io.quarkus:quarkus-rest-client")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("jakarta.validation:jakarta.validation-api:3.0.2")
  implementation("io.quarkus:quarkus-arc")
  implementation("com.github.docker-java:docker-java-core:3.6.0")
  implementation("com.github.docker-java:docker-java-transport-httpclient5:3.6.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
  testImplementation("io.quarkus:quarkus-junit5")
  testImplementation("io.rest-assured:rest-assured")
}

group = "com.connect4"
version = "1.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
  systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
allOpen {
  annotation("jakarta.ws.rs.Path")
  annotation("jakarta.enterprise.context.ApplicationScoped")
  annotation("jakarta.persistence.Entity")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
  compilerOptions {
    jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
    javaParameters = true
  }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
  freeCompilerArgs.set(listOf("-Xannotation-default-target=param-property"))
}
