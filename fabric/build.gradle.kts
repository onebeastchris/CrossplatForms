import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    id ("fabric-loom") version "1.2-SNAPSHOT" apply true
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    // fabric dependencies
    val minecraftVersion = "1.19.4"
    val loaderVersion = "0.14.21"
    val fabricVersion = "0.83.0+1.19.4"

    minecraft("com.mojang", "minecraft", minecraftVersion)
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)

    api(projects.proxy)
    api(projects.core)
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("cloud.commandframework", "dev.kejona.crossplatforms.shaded.cloud")
            relocate("org.spongepowered.configurate", "dev.kejona.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.kejona.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.kejona.crossplatforms.shaded.bstats")
        }
        exclude {
                e -> e.name.startsWith("com.mojang") // all available on velocity
                //|| e.name.startsWith("org.yaml")
                //|| e.name.startsWith("com.google")
                //|| e.name.startsWith("net.kyori")
        }
    }

    archiveFileName.set("CrossplatForms-Fabric.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "fabric"
