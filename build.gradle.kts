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

    repositories {
        maven {
            url = uri("https://nvm.tursom.cn/repository/forge-group/")
        }
        flatDir {
            dirs("libs")
        }
    }
}

subprojects {
    buildscript {
        repositories {
            maven {
                url = uri("https://nvm.tursom.cn/repository/forge-group/")
            }
            maven {
                url = uri("https://repo.spongepowered.org/maven")
            }
            maven { url = uri("https://modmaven.dev/") }
        }
        dependencies {
            classpath("net.minecraftforge.gradle:ForgeGradle:4.1.+") {
                isChanging = true
            }
            classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
        }
    }
}
