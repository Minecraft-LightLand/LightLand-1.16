plugins {
    id("eclipse")
    `maven-publish`
    kotlin("jvm")
    id("forge-gradle-kts")
}

apply(plugin = "net.minecraftforge.gradle")

configureForge {
    runs {
        createClient("clientQuest", project)
        createServer("serverQuest", project)
    }
}

useGeneratedResources()

dependencies {
    implementation(fg.deobf(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar")))))
    minecraft(project)
    core
    magic
    lombok
    junit
}

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
