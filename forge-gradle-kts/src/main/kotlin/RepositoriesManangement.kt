import org.gradle.api.Project

fun Project.useDependence() {
    when (ext.properties["dependence"]) {
        null, "china", "China", "CHINA" -> this.repositories.apply {
            maven {
                it.url = uri("https://nvm.tursom.cn/repository/forge-group/")
            }
            flatDir {
                it.dirs("libs")
            }
        }
        else -> this.repositories.apply {
            mavenCentral()
            jcenter()

            flatDir {
                it.dirs("libs")
            }
        }
    }
}