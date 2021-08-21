import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.internal.DefaultJavaLanguageVersion

class ForgeGradleKts : Plugin<Project> {
  override fun apply(target: Project) {
    target.afterEvaluate { project ->
      try {
        (project.extensions.getByName("java") as org.gradle.api.plugins.JavaPluginExtension).apply {
          toolchain { java ->
            java.languageVersion.set(DefaultJavaLanguageVersion.of(8))
          }
        }
      } catch (e: Exception) {
      }
    }
  }
}

val Project.fg
  get() = extensions.getByType(DependencyManagementExtension::class.java)


val Project.jeiVersion get() = property("jei_version") as String
val Project.mcVersion get() = property("mc_version") as String
val Project.forgeVersion get() = property("forge_version") as String