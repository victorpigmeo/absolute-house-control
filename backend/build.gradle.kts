plugins {
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.errorprone) apply false
}

// Resolved here (root script's own top-level scope, where the generated `libs` accessor is
// available) and captured by the subprojects{} closure below as plain values -- the accessor
// itself isn't usable from inside that closure, since it's only attached to a project once
// that project's own build script evaluates, which hasn't happened yet for the children here.
val errorproneCoreDependency = libs.errorprone.core
val googleJavaFormatVersion = libs.versions.google.java.format.get()

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "net.ltgt.errorprone")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "errorprone"(errorproneCoreDependency)
    }

    extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat(googleJavaFormatVersion)
            target("src/*/java/**/*.java")
        }
    }

    tasks.named<Test>("test") {
        useJUnitPlatform {
            excludeTags("integration")
        }
    }

    val sourceSets = extensions.getByType<SourceSetContainer>()

    tasks.register<Test>("integrationTest") {
        description = "Runs Testcontainers-backed integration tests (requires Docker)."
        group = "verification"
        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath
        useJUnitPlatform {
            includeTags("integration")
        }
        shouldRunAfter(tasks.named("test"))
    }
}
