import org.spongepowered.asm.gradle.plugins.MixinExtension

plugins {
    id("eclipse")
    `maven-publish`
    kotlin("jvm")
    id("forge-gradle-kts")
}

apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.spongepowered.mixin")

configure<MixinExtension> {
    add(sourceSets["main"], "lightland-magic.refmap.json")
}

repositories {
    maven {
        url = uri("https://maven.blamejared.com")
    }
    flatDir {
        dirs("libs")
    }
}

configureForge {
    runs {
        createClient("clientMagic", project) {
            properties(mapOf(
                    "mixin.env.remapRefMap" to "true",
                    "mixin.env.refMapRemappingFile" to "${buildDir}/createSrgToMcp/output.srg"
            ))
            arg("-mixin.config=lightland-magic.mixins.json")
        }
        createServer("serverMagic", project) {
            properties(mapOf(
                    "mixin.env.remapRefMap" to "true",
                    "mixin.env.refMapRemappingFile" to "${buildDir}/createSrgToMcp/output.srg"
            ))
            arg("-mixin.config=lightland-magic.mixins.json")
        }
    }
}

useGeneratedResources()

dependencies {
    minecraft(project)
    core
    lombok
    junit
    mixin

    compileOnly(fg.deobf("mezz.jei:jei-${mcVersion}:${jeiVersion}:api"))
    compileOnly(fg.deobf("net.darkhax.bookshelf:Bookshelf-${mcVersion}:10.2.15"))
    compileOnly(fg.deobf("net.darkhax.gamestages:GameStages-${mcVersion}:7.2.8"))
    compileOnly(fg.deobf("net.darkhax.itemstages:ItemStages-Forge-${mcVersion}:3.0.6"))
    compileOnly(fg.deobf("vazkii.patchouli:Patchouli:1.16.4-53.1"))
    compileOnly(fg.deobf("zip.local.ap:Apotheosis-1.16.4:4.6.1"))

    runtimeOnly(fg.deobf("mezz.jei:jei-${mcVersion}:${jeiVersion}"))
    runtimeOnly(fg.deobf("net.darkhax.bookshelf:Bookshelf-${mcVersion}:10.2.15"))
    runtimeOnly(fg.deobf("net.darkhax.gamestages:GameStages-${mcVersion}:7.2.8"))
    runtimeOnly(fg.deobf("net.darkhax.itemstages:ItemStages-Forge-${mcVersion}:3.0.6"))
    runtimeOnly(fg.deobf("vazkii.patchouli:Patchouli:1.16.4-53.1"))
    runtimeOnly(fg.deobf("zip.local.ap:Apotheosis-1.16.4:4.6.1"))

    runtimeOnly(fg.deobf("zip.local.tf:twilightforest-${mcVersion}:4.0.546-universal"))
    runtimeOnly(fg.deobf("zip.local.mt:mahoutsukai-1.16.5:v1.31.37"))
    runtimeOnly(fg.deobf("zip.local.pl:Placebo-1.16.4:4.5.0"))


}

// Example for how to get properties into the manifest for reading by the runtime..
jar {
    defaultManifest(project)
    manifest {
        attributes(
                attributes + mapOf(
                        "MixinConfigs" to "lightland-magic.mixins.json"
                )
        )
    }
    finalizedBy("reobfJar")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.getByName("jar"))
        }
    }
    repositories {
        maven {
            url = uri("file:///${project.projectDir}/mcmodsrepo")
        }
    }
}

disableTests()
excludeReobfJar()

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
