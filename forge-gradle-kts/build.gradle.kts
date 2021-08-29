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
  maven {
    url = uri("https://repo.spongepowered.org/maven")
  }
}

dependencies {
  api("net.minecraftforge.gradle:ForgeGradle:4.1.14")
  api("org.spongepowered:mixingradle:0.7-SNAPSHOT")
  api("com.google.code.gson:gson:2.8.8")
  api(group = "org.yaml", name = "snakeyaml", version = "1.29")
}

