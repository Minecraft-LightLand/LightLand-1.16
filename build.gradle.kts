//repositories {
//    maven {
//        url = uri("https://nvm.tursom.cn/repository/forge-group/")
//    }
//    maven {
//        url = uri("https://repo.spongepowered.org/maven")
//    }
//    maven { url = uri("https://modmaven.dev/") }
//}

plugins {
    java
    id("eclipse")
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    //id("forge-gradle-kts")
}

println(
    "Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
        "java.vendor"
    ) + ") Arch: " + System.getProperty("os.arch")
)

repositories {
    maven {
        url = uri("https://nvm.tursom.cn/repository/forge-group/")
    }
}

allprojects {
    version = "0.4.18"
    group = "com.hikarishima"

    when (ext.properties["dependence"]) {
        null, "china", "China", "CHINA" -> repositories {
            maven {
                url = uri("https://nvm.tursom.cn/repository/forge-group/")
            }
            flatDir {
                dirs("libs")
            }
        }
        else -> repositories {
            mavenCentral()
            jcenter()
            maven { url = uri("https://maven.blamejared.com") }
            maven { url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven") }
            maven { url = uri("https://www.cursemaven.com") }
            maven { url = uri("https://maven.minecraftforge.net") }
            //maven { url = uri("https://plugins.gradle.org/m2") }
            maven { url = uri("https://modmaven.dev/") }
            maven { url = uri("https://dvs1.progwml6.com/files/maven/") }
            maven { url = uri("https://repo.spongepowered.org/maven") }
            maven { url = uri("https://maven.theillusivec4.top/") }
            //maven { url = uri("") }

            flatDir {
                dirs("libs")
            }
        }
    }
    //repositories {
    //    maven {
    //        url = uri("https://nvm.tursom.cn/repository/forge-group/")
    //    }
    //    flatDir {
    //        dirs("libs")
    //    }
    //}
}

subprojects {
    buildscript {
        when (ext.properties["dependence"]) {
            null, "china", "China", "CHINA" -> repositories {
                maven {
                    url = uri("https://nvm.tursom.cn/repository/forge-group/")
                }
            }
            else -> repositories {
                mavenCentral()
                jcenter()
                maven { url = uri("https://maven.blamejared.com") }
                maven { url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven") }
                maven { url = uri("https://www.cursemaven.com") }
                maven { url = uri("https://maven.minecraftforge.net") }
                maven { url = uri("https://plugins.gradle.org/m2") }
                maven { url = uri("https://modmaven.dev/") }
                maven { url = uri("https://dvs1.progwml6.com/files/maven/") }
                maven { url = uri("https://repo.spongepowered.org/maven") }
                maven { url = uri("https://maven.theillusivec4.top/") }
                //maven { url = uri("") }
            }
        }
        dependencies {
            classpath("net.minecraftforge.gradle:ForgeGradle:4.1.+") {
                isChanging = true
            }
            classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
        }
    }
}
