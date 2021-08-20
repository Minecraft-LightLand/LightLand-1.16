plugins {
  kotlin("jvm") version "1.3.72"
  `java-gradle-plugin`
}

gradlePlugin {
  plugins {
    create("forge-gradle-kts") {
      // 在 app 模块需要通过 id 引用这个插件
      id = "forge-gradle-kts"
      // 实现这个插件的类的路径
      implementationClass = "ForgeGradleKts"
    }
  }
}

repositories {
  maven {
    url = uri("https://nvm.tursom.cn/repository/maven-public/")
  }
}

dependencies {
  api("net.minecraftforge.gradle:ForgeGradle:4.1.14")
}

