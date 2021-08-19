plugins {
  // id("net.minecraftforge.gradle.forge") version "2.0.2"
  id("eclipse")
  `maven-publish`
  kotlin("jvm") version "1.5.21"
}

version = "1.0"
group = "com.hikarishima" // http://maven.apache.org/guides/mini/guide-naming-conventions.html

println("Java: " + System.getProperty("java.version") + " JVM: " + System.getProperty("java.vm.version") + "(" + System.getProperty(
  "java.vendor") + ") Arch: " + System.getProperty("os.arch"))

repositories {
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}
