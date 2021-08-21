import net.minecraftforge.gradle.userdev.UserDevExtension
import java.text.SimpleDateFormat
import java.util.*

buildscript {
  repositories {
    // maven {
    //   url = uri("https://maven.minecraftforge.net")
    // }
    maven {
      url = uri("https://nvm.tursom.cn/repository/maven-public/")
    }
    // mavenCentral()
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:4.1.+") {
      isChanging = true
    }
  }
}

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

// java.toolchain.languageVersion = JavaLanguageVersion.of(8) // Mojang ships Java 8 to end users, so your mod should target Java 8.

val mcVersion get() = project.property("mc_version") as String
val forgeVersion get() = project.property("forge_version") as String

println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
  "java.vendor") + ") Arch: " + System.getProperty("os.arch"))
configure<UserDevExtension> {
  // The mappings can be changed at any time, and must be in the following format.
  // Channel:   Version:
  // snapshot   YYYYMMDD   Snapshot are built nightly.
  // stable     #          Stables are built at the discretion of the MCP team.
  // official   MCVersion  Official field/method names from Mojang mapping files
  //
  // You must be aware of the Mojang license when using the "official" mappings.
  // See more information here: https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md
  //
  // Use non-default mappings at your own risk. they may not always work.
  // Simply re-run your setup task after changing the mappings to update your workspace.
  mappings("official", "1.16.5")
  // makeObfSourceJar = false // an Srg named sources jar is made by default. uncomment this to disable.

  // accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")

  // Default run configurations.
  // These can be tweaked, removed, or duplicated as needed.
  runs {
    create("client") {
      taskName = "runClientTerrain"
      workingDirectory(project.file("run"))

      // Recommended logging data for a userdev environment
      // The markers can be changed as needed.
      // "SCAN": For mods scan.
      // "REGISTRIES": For firing of registry events.
      // "REGISTRYDUMP": For getting the contents of all registries.
      property("forge.logging.markers", "REGISTRIES")

      // Recommended logging level for the console
      // You can set various levels here.
      // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
      property("forge.logging.console.level", "debug")

      mods {
        create("examplemod") {
          source(sourceSets["main"])
        }
      }
    }
  }
}

// Include resources generated by data generators.
sourceSets["main"].resources {
  srcDir("src/generated/resources")
}

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

// Example configuration to allow publishing using the maven-publish task
// This is the preferred method to reobfuscate your jar file
// However if you are in a multi-project build, dev time needs unobfed jar files, so you can delay the obfuscation until publishing by doing
//publish.dependsOn("reobfJar")

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
