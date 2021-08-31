import org.spongepowered.asm.gradle.plugins.MixinExtension
import org.spongepowered.asm.gradle.plugins.MixinGradlePlugin

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

configureForge {
    runs {
        createClient("clientMagic", project) {
            arg("-mixin.config=lightland-magic.mixins.json")
        }
        createServer("serverMagic", project) {
            arg("-mixin.config=lightland-magic.mixins.json")
        }
    }
}

useGeneratedResources()

dependencies {
    compileOnly(fg.deobf(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar")))))
    minecraft(project)
    core
    lombok
    jei(project)
    compileOnly(fg.deobf("net.darkhax.gamestages:GameStages-$mcVersion:7.2.8"))
    junit
    mixin
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
