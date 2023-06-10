
plugins {
    java
    `java-library`
    `maven-publish`
    id("net.kyori.indra.git") // used for getting branch/commit info
    id("idea") // used to download sources and documentation
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "net.kyori.indra.git")

    group = "dev.kejona"
    version = "1.5.0"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(8)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        // Disable creating of test report files
        reports.html.required.set(false)
        reports.junitXml.required.set(false)
    }

    tasks.named("build") {
        dependsOn(tasks.named<Test>("test"))
    }

    // Ensure platform jars are flagged as multi release
    tasks.jar {
        manifest {
            attributes("Multi-Release" to "true")
        }
    }

    tasks.processResources {
        expand(
            "project_description" to "Bedrock Edition forms, inventory menus, and more.",
            "project_url" to "https://github.com/kejonaMC/CrossplatForms",
            "project_version" to project.version,

            // indra branch works locally, environment variable should work on jenkins.
            "git_branch" to (indraGit.branchName() ?: System.getenv("GIT_BRANCH")),
            "git_commit" to (indraGit.commit()?.abbreviate(7)?.name() ?: "UNKNOWN"),
            "build_number" to (System.getenv("BUILD_NUMBER") ?: "UNKNOWN")
        )
    }

    // disable javadocs
    tasks.withType<Javadoc>().all { enabled = false }

    repositories {
        mavenCentral()
        maven("https://repo.opencollab.dev/main/") // geyser etc
        maven("https://jitpack.io") // fixes issue with Cloudburst Protocol that geyser depends on
        maven("https://libraries.minecraft.net/") // brigadier

        maven("https://oss.sonatype.org/content/repositories/snapshots") // bungeecord, spigot
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
        maven("https://repo.codemc.io//repository/maven-public/") // NBT api

        maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/") // protocolize for proxies
        maven("https://repo.papermc.io/repository/maven-public/") // velocity

        maven("https://maven.fabricmc.net/") // fabric
    }
}

subprojects {
    dependencies {
        testAnnotationProcessor("org.projectlombok:lombok:1.18.26")
        testCompileOnly("org.projectlombok:lombok:1.18.26")
        testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

        annotationProcessor("org.projectlombok:lombok:1.18.26")
        compileOnly("org.projectlombok:lombok:1.18.26")
        compileOnly("com.google.code.findbugs:jsr305:3.0.2") // nullability annotations
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
