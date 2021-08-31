plugins {
    id("eclipse")
    `maven-publish`
    kotlin("jvm")
    id("forge-gradle-kts")
}

apply(plugin = "net.minecraftforge.gradle")

configureForge {
    runs {
        createClient("clientTerrain", project)
        createServer("serverTerrain", project)
    }
}

useGeneratedResources()

dependencies {
    implementation(fg.deobf(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar")))))
    minecraft(project)
    core
    jei(project)
    lombok
    junit
}

// Example for how to get properties into the manifest for reading by the runtime..
jar {
    defaultManifest(project)
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
