plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    id("maven-publish")
}

version = property("mod_version") as String
group = property("maven_group") as String

val archivesBaseName = property("archives_base_name") as String
val modVersionForExpansion = version.toString()

base {
    archivesName.set(archivesBaseName)
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create("barium") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "ClothConfig"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "ModMenu"
        url = uri("https://maven.terraformersmc.com/releases/")
    }
}

dependencies {
    val minecraftVersion = property("minecraft_version") as String
    val loaderVersion = property("loader_version") as String
    val fabricApiVersion = property("fabric_api_version") as String
    val modmenuVersion = property("modmenu_version") as String
    val clothConfigVersion = property("cloth_config_version") as String

    minecraft("com.mojang:minecraft:$minecraftVersion")
    // 26.1 já roda em ambiente não-ofuscado no Loom 1.16, então não é permitido
    // forçar officialMojangMappings() explicitamente.

    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    implementation("com.terraformersmc:modmenu:$modmenuVersion")
    implementation("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion")
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to modVersionForExpansion)
    }
}

val targetJavaVersion = 25

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
    options.isDeprecation = true
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_$archivesBaseName" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = archivesBaseName
            from(components["java"])
        }
    }

    repositories {
        // Repositórios para publicação viriam aqui
    }
}
