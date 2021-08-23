plugins {
    id("eclipse")
    `maven-publish`
    kotlin("jvm")
    id("forge-gradle-kts")
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
            args(
                "--mod",
                "lightland",
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/main/resources/")
            )
        }
    }
}

useGeneratedResources()

dependencies {
    api(kotlin("stdlib"))
    api(kotlin("reflect"))
    minecraft(project)
    jei(project)
    compileOnly(fg.deobf("net.darkhax.gamestages:GameStages-$mcVersion:7.2.8"))
    lombok
    junit
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
