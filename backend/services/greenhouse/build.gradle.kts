plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

// io.spring.dependency-management doesn't honor Gradle-native platform() BOM imports for
// nested BOMs -- testcontainers-bom is imported via this DSL instead.
dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
    }
}

dependencies {
    implementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-flyway")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
    // wiremock-standalone, not plain wiremock/wiremock-jetty12: those leave Jetty as a regular
    // (non-shaded) dependency, which io.spring.dependency-management's project-wide BOM override
    // then bumps inconsistently across WireMock's own Jetty modules (ABI-incompatible mix of
    // jetty-core 12.1.10 + jetty-ee10-servlet 12.0.30) -- a NoSuchMethodError at runtime.
    // wiremock-standalone shades/relocates its bundled Jetty so the BOM can't touch it.
    testImplementation("org.wiremock:wiremock-standalone:${libs.versions.wiremock.get()}")
}
