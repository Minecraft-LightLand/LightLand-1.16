import net.minecraftforge.gradle.userdev.UserDevExtension
import java.text.SimpleDateFormat
import java.util.*

plugins {
  id("eclipse")
  `maven-publish`
  kotlin("jvm")
  id("forge-gradle-kts")
}

apply(plugin = "net.minecraftforge.gradle")

val fg = project.extensions.getByType<net.minecraftforge.gradle.userdev.DependencyManagementExtension>()

version = "1.0"
group = "com.hikarishima" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

val mcVersion get() = project.property("mc_version") as String
val forgeVersion get() = project.property("forge_version") as String

println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
  "java.vendor") + ") Arch: " + System.getProperty("os.arch"))
configure<UserDevExtension> {
  mappings("official", "1.16.5")

  runs {
    create("client") {
      taskName = "runClientTerrain"
      workingDirectory(project.file("run"))

      property("forge.logging.markers", "REGISTRIES")
      property("forge.logging.console.level", "debug")

      mods {
        create("examplemod") {
          source(sourceSets["main"])
        }
      }
    }
  }
}

useGeneratedResources()

repositories {
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}

dependencies {
  api(project(":lightland-core"))
  "minecraft"("net.minecraftforge:forge:${mcVersion}-${forgeVersion}")

  compileOnly(fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion:api"))
  runtimeOnly(fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion"))

  compileOnly("org.projectlombok:lombok:1.18.20")
  annotationProcessor("org.projectlombok:lombok:1.18.20")

  testCompileOnly("org.projectlombok:lombok:1.18.20")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.20")
  testImplementation(group = "junit", name = "junit", version = "4.13.2")
}

// Example for how to get properties into the manifest for reading by the runtime..
tasks.getByName("jar") {
  this as Jar
  manifest {
    @Suppress("SpellCheckingInspection")
    attributes(mapOf(
      "Specification-Title" to "lightland",
      "Specification-Vendor" to "hikarishima",
      "Specification-Version" to "1", // We are version 1 of ourselves
      "Implementation-Title" to project.name,
      "Implementation-Version" to archiveVersion.get(),
      "Implementation-Vendor" to "hikarishima",
      "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
    ))
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

if (project.gradle.startParameter.taskNames.find { taskName ->
    ":test" in taskName
  } == null) {
  project.tasks {
    test { enabled = false }
    testClasses { enabled = false }
    compileTestJava { enabled = false }
    //compileTestKotlin { enabled = false }
    processTestResources { enabled = false }
  }
}
