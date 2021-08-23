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
    }
}

useGeneratedResources()

dependencies {
    minecraft(project)
    core
    magic
    jei(project)
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
