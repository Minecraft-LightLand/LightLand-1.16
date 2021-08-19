import net.minecraftforge.gradle.userdev.UserDevExtension

buildscript {
  repositories {
    maven(url = "https://nvm.tursom.cn/repository/maven-public/")
    // mavenCentral()
    // maven(url = "https://maven.minecraftforge.net")
  }
  dependencies {
    classpath("net.minecraftforge.gradle:ForgeGradle:4.1.+") {
      isChanging = true
    }
  }
}

plugins {
  // id("net.minecraftforge.gradle.forge") version "2.0.2"
  id("eclipse")
  `maven-publish`
  kotlin("jvm") version "1.5.21"
}

apply {
  plugin("net.minecraftforge.gradle")
}
// // Only edit below this line, the above code adds and enables the necessary things for Forge to be setup.
// apply(plugin = "eclipse")
// apply(plugin = "maven-publish")

version = "1.0"
group = "com.hikarishima.lightland" // http://maven.apache.org/guides/mini/guide-naming-conventions.html
// archivesBaseName = "lightland"

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
  // TODO
  mappings(mapOf(
    "channel" to "official",
    "version" to "1.16.5"
  ))
  // makeObfSourceJar = false // an Srg named sources jar is made by default. uncomment this to disable.

  // accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")

  // Default run configurations.
  // These can be tweaked, removed, or duplicated as needed.
  runs {
    create("client") {
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
        }
        // examplemod {
        //   source(sourceSets.main)
        // }
      }
    }

    create("server") {
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
        // examplemod {
        //   source(sourceSets.main)
        // }
      }
    }

    create("data") {
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

      // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
      args("--mod",
        "lightland",
        "--all",
        "--output",
        file("src/generated/resources/"),
        "--existing",
        file("src/main/resources/"))

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
  // maven {
  //   // location of the maven that hosts JEI files
  //   name = "Progwml6 maven"
  //   url = uri("https://dvs1.progwml6.com/files/maven/")
  // }
  // maven {
  //   url = uri("https://maven.blamejared.com")
  // }
  // maven {
  //   // location of a maven mirror for JEI files, as a fallback
  //   name = "ModMaven"
  //   url = uri("https://modmaven.k-4u.nl")
  // }
}

dependencies {
  // Specify the version of Minecraft to use, If this is any group other then "net.minecraft" it is assumed
  // that the dep is a ForgeGradle "patcher" dependency. And it"s patches will be applied.
  // The userdev artifact is a special name and will get all sorts of transformations applied to it.
  // println(project.property("forge_version"))
  "minecraft"("net.minecraftforge:forge:${mcVersion}-${forgeVersion}")

  // You may put jars on which you depend on in ./libs or you may define them like so..
  // compile "some.group:artifact:version:classifier"
  // compile "some.group:artifact:version"

  // compile against the JEI API but do not include it at runtime

  println("fg: ${this.javaClass.methods.asList()}")

  // compileOnly(deobf("mezz.jei:jei-${mcVersion}:${jei_version}:api"))
  // at runtime, use the full JEI jar
  // runtimeOnly(deobf("mezz.jei:jei-${mcVersion}:${jei_version}"))
  //
  //
  // compile(fg.deobf("net.darkhax.gamestages:GameStages-1.16.5:7.2.8"))

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
// jar {
//   manifest {
//     // attributes([
//     //   "Specification-Title"     : "lightland",
//     // "Specification-Vendor"    : "hikarishima",
//     // "Specification-Version"   : "1", // We are version 1 of ourselves
//     // "Implementation-Title"    : project.name,
//     // "Implementation-Version"  : "${version}",
//     // "Implementation-Vendor"   : "hikarishima",
//     // "Implementation-Timestamp": new Date().format("yyyy-MM-dd"T"HH:mm:ssZ")
//     // ])
//   }
// }

// Example configuration to allow publishing using the maven-publish task
// This is the preferred method to reobfuscate your jar file
// jar.finalizedBy("reobfJar")
// However if you are in a multi-project build, dev time needs unobfed jar files, so you can delay the obfuscation until publishing by doing
//publish.dependsOn("reobfJar")

// publishing {
//   publications {
//     mavenJava(MavenPublication) {
//       artifact(sun.tools.jar.resources.jar)
//     }
//   }
//   repositories {
//     maven {
//       url = uri("file:///${project.projectDir}/mcmodsrepo")
//     }
//   }
// }

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
