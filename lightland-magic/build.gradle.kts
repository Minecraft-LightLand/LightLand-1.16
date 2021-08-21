import net.minecraftforge.gradle.userdev.UserDevExtension
import java.text.SimpleDateFormat
import java.util.*

//buildscript {
//  repositories {
//    maven {
//      url = uri("https://maven.minecraftforge.net")
//    }
//    maven {
//      url = uri("https://nvm.tursom.cn/repository/maven-public/")
//    }
//    // mavenCentral()
//  }
//  dependencies {
//    classpath("net.minecraftforge.gradle:ForgeGradle:4.1.+") {
//      isChanging = true
//    }
//  }
//}

plugins {
  id("eclipse")
  `maven-publish`
  kotlin("jvm")
  id("forge-gradle-kts")
}

apply(plugin = "net.minecraftforge.gradle")

version = "1.0"
group = "com.hikarishima" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

// val mcVersion get() = project.property("mc_version") as String
// val forgeVersion get() = project.property("forge_version") as String

// println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
//   "java.vendor") + ") Arch: " + System.getProperty("os.arch"))
configure<UserDevExtension> {
  mappings("official", "1.16.5")
  runs {
    create("client") {
      taskName = "runClientMagic"
      workingDirectory(rootProject.file("run"))
      property("forge.logging.markers", "REGISTRIES")

      property("forge.logging.console.level", "debug")

      mods {
        create("lightland-magic") {
          source(sourceSets["main"])
        }
      }
    }
  }
}

// Include resources generated by data generators.
sourceSets["main"].resources {
  // val core = rootProject.project("lightland-core")!!
  // srcDir("${core.path}/src/generated/resources")
  srcDir("src/generated/resources")
}

repositories {
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}

dependencies {
  // Specify the version of Minecraft to use, If this is any group other then "net.minecraft" it is assumed
  // that the dep is a ForgeGradle "patcher" dependency. And it"s patches will be applied.
  // The userdev artifact is a special name and will get all sorts of transformations applied to it.
  // println(project.property("forge_version"))
  "minecraft"("net.minecraftforge:forge:${mcVersion}-${forgeVersion}")

  api(project(":lightland-core"))

  val jeiVersion = project.property("jei_version") as String
  compileOnly(fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion:api"))
  runtimeOnly(fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion"))

  compileOnly(fg.deobf("net.darkhax.gamestages:GameStages-$mcVersion:7.2.8"))

  // compile "com.mod-buildcraft:buildcraft:6.0.8:dev"  // adds buildcraft to the dev env
  // compile "com.googlecode.efficient-java-matrix-library:ejml:0.24" // adds ejml to the dev env

  // The "provided" configuration is for optional dependencies that exist at compile-time but might not at runtime.
  // provided "com.mod-buildcraft:buildcraft:6.0.8:dev"

  // These dependencies get remapped to your current MCP mappings
  // deobf "com.mod-buildcraft:buildcraft:6.0.8:dev"

  // For more info...
  // http://www.gradle.org/docs/current/userguide/artifact_dependencies_tutorial.html
  // http://www.gradle.org/docs/current/userguide/dependency_management.html

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
      "Specification-Title" to "lightland-magic",
      "Specification-Vendor" to "hikarishima",
      "Specification-Version" to "1", // We are version 1 of ourselves
      "Implementation-Title" to project.name,
      "Implementation-Version" to archiveVersion.get(),
      "Implementation-Vendor" to "hikarishima",
      "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
    ))
  }
  from("resources") {
    include("**")
  }
  // finalizedBy("reobfJar")
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

// tasks.whenTaskAdded {
//   if ("prepareRuns" in name) {
//     dependsOn(rootProject.project(":lightland-core").tasks.findByName("prepareRuns"))
//   }
// }

// evaluationDependsOn(":lightland-core")

tasks.whenTaskAdded {
  if (name == "reobfJar") {
    enabled = false
  }
}
