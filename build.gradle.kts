plugins {
    java
    id("eclipse")
    `maven-publish`
    kotlin("jvm") version "1.5.30"
}

println(
        "Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
                "java.vendor"
        ) + ") Arch: " + System.getProperty("os.arch")
)

repositories {
    maven {
        url = uri("https://nvm.tursom.cn/repository/maven-public/")
    }
}

allprojects {
    version = "0.4.8"
    group = "com.hikarishima"

    repositories {
        maven {
            url = uri("https://nvm.tursom.cn/repository/maven-public/")
        }
    }
}

subprojects {
    buildscript {
        repositories {
            maven {
                url = uri("https://nvm.tursom.cn/repository/maven-public/")
            }
            maven {
                url = uri("https://repo.spongepowered.org/maven")
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
