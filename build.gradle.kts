plugins {
  java
  id("eclipse")
  `maven-publish`
  kotlin("jvm") version "1.5.21"
}

println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
  "java.vendor") + ") Arch: " + System.getProperty("os.arch"))

repositories {
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}

allprojects {
  version = "1.0"
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
        url = uri("https://maven.minecraftforge.net")
      }
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

  // apply(plugin = "net.minecraftforge.gradle")
}
