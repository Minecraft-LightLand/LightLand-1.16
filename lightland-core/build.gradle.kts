plugins {
    id("eclipse")
    `maven-publish`
    kotlin("jvm")
    id("forge-gradle-kts")
    id("com.github.johnrengelman.shadow") version "6.1.0"
    kotlin("plugin.allopen") version "1.5.30"
}

apply(plugin = "net.minecraftforge.gradle")

configureForge {
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
    //mappings("official", "1.16.5")
    // makeObfSourceJar = false // an Srg named sources jar is made by default. uncomment this to disable.

    // accessTransformer = file("src/main/resources/META-INF/accesstransformer.cfg")

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        createClient("clientCore", project)
        createServer("serverCore", project)
        createData("dataCore", project) {
            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            args.addAll(listOf(
                "--mod",
                "lightland",
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            ))
        }
    }
}

useGeneratedResources()

dependencies {
    implementation(fg.deobf(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar")))))
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    minecraft(project)
    lombok
    junit

    implementation("cglib:cglib:3.3.0")
    implementation("com.esotericsoftware:reflectasm:1.11.9")
}

tasks.getByName("shadowJar") {
    if (gradle.startParameter.taskNames.find { taskName ->
            "reobfJar" in taskName || "shadowJar" in taskName
        } == null) {
        enabled = false
    }
    this as com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
    archiveClassifier.set("")
    // append("")

    dependencies {
        include(dependency("cglib:cglib:3.3.0"))
        include(dependency("com.esotericsoftware:reflectasm:1.11.9"))
        //include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.5.30"))
        //include(dependency("org.jetbrains.kotlin:kotlin-reflect:1.5.30"))
        //include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1"))
    }
}

// Example for how to get properties into the manifest for reading by the runtime..
jar {
    defaultManifest(project)
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
            url = uri("file:///${rootProject.projectDir}/mcmodsrepo")
        }
    }
}

disableTests()
excludeReobfJar()

tasks.whenTaskAdded {
    if ("reobfJar" in name) {
        dependsOn("shadowJar")
        // tasks.getByName("shadowJar").dependsOn(path)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

allOpen {
    annotations("cn.tursom.forge.Open")
}
