import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
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

fun NamedDomainObjectContainer<RunConfig>.createClient(
    name: String = "client",
    configureAction: Action<RunConfig>? = null
) {
    create("client") {
        it.taskName = if (name.startsWith("run")) {
            name
        } else if (name.isEmpty()) {
            "runClient"
        } else {
            "run${name[0].toUpperCase()}${name.substring(1)}"
        }
        configureAction?.execute(it)
    }
}

fun NamedDomainObjectContainer<RunConfig>.createServer(
    name: String = "server",
    configureAction: Action<RunConfig>? = null
) {
    create("server") {
        it.taskName = if (name.startsWith("run")) {
            name
        } else if (name.isEmpty()) {
            "runServer"
        } else {
            "run${name[0].toUpperCase()}${name.substring(1)}"
        }
        configureAction?.execute(it)
    }
}

fun NamedDomainObjectContainer<RunConfig>.createData(
    name: String = "data",
    configureAction: Action<RunConfig>? = null
) {
    create("data") {
        it.taskName = if (name.startsWith("run")) {
            name
        } else if (name.isEmpty()) {
            "runData"
        } else {
            "run${name[0].toUpperCase()}${name.substring(1)}"
        }
        configureAction?.execute(it)
    }
}

/**
 * 添加生成的resources文件夹
 */
fun Project.useGeneratedResources() {
    (extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer).getByName("main").resources {
        it.srcDir("src/generated/resources")
        it.srcDir("${rootProject.projectDir.absolutePath}/src/main/resources")
    }
}

/**
 * 禁用混淆 jar
 */
fun Project.excludeReobfJar() {
    tasks.whenTaskAdded {
        if (it.name == "reobfJar") {
            it.enabled = false
        }
    }
}
