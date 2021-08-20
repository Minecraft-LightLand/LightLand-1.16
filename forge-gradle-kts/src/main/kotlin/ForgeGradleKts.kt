import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.internal.DefaultJavaLanguageVersion

class ForgeGradleKts : Plugin<Project> {
  override fun apply(target: Project) {
    target.afterEvaluate {
      try {
        (it.extensions.getByName("java") as org.gradle.api.plugins.JavaPluginExtension).apply {
          toolchain {
            it.languageVersion.set(DefaultJavaLanguageVersion.of(8))
          }
        }
      } catch (e: Exception) {
      }
    }
  }
}

val Project.fg
  get() = extensions.getByType(DependencyManagementExtension::class.java)

