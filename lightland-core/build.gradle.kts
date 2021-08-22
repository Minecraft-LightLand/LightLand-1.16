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

// val mcVersion get() = project.property("mc_version") as String
// val forgeVersion get() = project.property("forge_version") as String

// println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
//   "java.vendor") + ") Arch: " + System.getProperty("os.arch"))
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
        createClient("core") {
            workingDirectory(rootProject.file("run"))

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

        create("server") {
            workingDirectory(rootProject.file("run"))

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

        create("data") {
            workingDirectory(rootProject.file("run"))

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
            args(
                "--mod",
                "lightland",
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/main/resources/")
            )

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

    api(kotlin("stdlib"))
    api(kotlin("reflect"))

    // You may put jars on which you depend on in ./libs or you may define them like so..
    // compile "some.group:artifact:version:classifier"
    // compile "some.group:artifact:version"

    // compile against the JEI API but do not include it at runtime

    val jeiVersion = project.property("jei_version") as String
    compileOnly(project.fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion:api"))
    // at runtime, use the full JEI jar
    runtimeOnly(fg.deobf("mezz.jei:jei-$mcVersion:$jeiVersion"))

    // api(fg.deobf("net.darkhax.bookshelf:Bookshelf-$mcVersion:7.2.8"))
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
        attributes(
            mapOf(
                "Specification-Title" to "lightland",
                "Specification-Vendor" to "hikarishima",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion.get(),
                "Implementation-Vendor" to "hikarishima",
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
            )
        )
    }
    // dependsOn("disableReobfJar")
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
            url = uri("file:///${rootProject.projectDir}/mcmodsrepo")
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

// tasks.create("disableReobfJar") {
//   try {
//     tasks.getByName("reobfJar").enabled = false
//   } catch (e: Exception) {
//   }
// }

excludeReobfJar()
