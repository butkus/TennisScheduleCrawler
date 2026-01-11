plugins {
    java
    idea
    id("org.springframework.boot") version Versions.springBoot
    id("io.spring.dependency-management") version Versions.springDependencyManagementPlugin
    id("org.kordamp.gradle.source-stats") version Versions.sourceStats
}

group = "com.butkus"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    testAnnotationProcessor("org.projectlombok:lombok:${Versions.lombok}")

    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Versions.lombok}")

    implementation("org.springframework.boot:spring-boot-starter-web:${Versions.springBoot}")
    implementation("org.apache.httpcomponents.client5:httpclient5:${Versions.http5Client}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${Versions.jacksonAnnotations}")
    implementation("org.apache.commons:commons-lang3:${Versions.commonsLang3}")
    implementation("commons-io:commons-io:${Versions.commonsIo}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${Versions.springBoot}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
}

tasks.test {
    useJUnitPlatform()
}

idea.module {
    isDownloadJavadoc = true
}