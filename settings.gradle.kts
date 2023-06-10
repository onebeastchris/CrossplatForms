enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CrossplatForms"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") // loom
    }
    plugins {
        id("net.kyori.indra.git") version "3.0.1" // getting branch and commit info at compile time
        id("net.kyori.indra") version "3.0.1" // multi-release jar for referencing Java 16 geyser api
        id("com.github.johnrengelman.shadow") version "7.1.2" // shadowing dependencies
        id("fabric-loom") version "1.2-SNAPSHOT" apply false// fabric
    }
}

include(":core")
include(":access-item")

include(":proxy")
include(":bungeecord")
include(":velocity")

include(":spigot")

include(":spigot-common:common")
findProject(":spigot-common:common")?.name = "common"

include(":spigot-common:v1_8_R3")
findProject(":spigot-common:v1_8_R3")?.name = "v1_8_R3"

include(":spigot-common:v1_9_R2")
findProject(":spigot-common:v1_9_R2")?.name = "v1_9_R2"

include(":spigot-common:v1_12_R1")
findProject(":spigot-common:v1_12_R1")?.name = "v1_12_R1"

include(":spigot-common:v1_13_R2")
findProject(":spigot-common:v1_13_R2")?.name = "v1_13_R2"

include(":spigot-common:v1_14_R1")
findProject(":spigot-common:v1_14_R1")?.name = "v1_14_R1"

include(":fabric")