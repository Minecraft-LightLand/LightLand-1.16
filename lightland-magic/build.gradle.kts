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
    flatDir {
        dirs("libs")
    }
}

configureForge {
    runs {
        createClient("clientMagic", project) {
            properties(mapOf("mixin.env.disableRefMap" to "true"))
            arg("-mixin.config=lightland-magic.mixins.json")
        }
        createServer("serverMagic", project) {
            properties(mapOf("mixin.env.disableRefMap" to "true"))
            arg("-mixin.config=lightland-magic.mixins.json")
        }
    }
}

useGeneratedResources()

dependencies {
    minecraft(project)
    core
    lombok
    jei(project)
    gameStages(project)
    junit
    mixin
    implementation(fg.deobf("zip.local.citadel:citadel-1.7.3-1.16.5:1.7.3"))
    implementation(fg.deobf("zip.local.tf:twilightforest-1.16.5:4.0.546-universal"))
    implementation(fg.deobf("zip.local.iaf:iceandfire-2.1.8-1.16.5:2.1.8"))
    //implementation(fg.deobf("zip.local.dg:dungeons_gear-1.16.5:3.0.17"))
    implementation(fg.deobf("zip.local.mt:mahoutsukai-1.16.5:v1.31.37"))
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
