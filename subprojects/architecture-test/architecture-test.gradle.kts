import gradlebuild.basics.PublicApi

plugins {
    id("gradlebuild.internal.java")
    id("gradlebuild.binary-compatibility")
}

dependencies {
    testImplementation(project(":baseServices"))
    testImplementation(project(":modelCore"))

    testImplementation(testLibrary("archunit_junit4"))
    testImplementation(library("guava"))

    testRuntimeOnly(project(":distributionsFull"))
}

tasks.withType<Test>().configureEach {
    // Looks like loading all the classes requires more than the default 512M
    maxHeapSize = "700M"

    systemProperty("org.gradle.public.api.includes", PublicApi.includes.joinToString(":"))
    systemProperty("org.gradle.public.api.excludes", PublicApi.excludes.joinToString(":"))
}


/*
open class BuildStructureViz: DefaultTask() {

    private
    val declaredDistributions = mutableMapOf<String, List<Project>>()

    private
    val allDependencies = mutableMapOf<String, Set<String>>()

    @Input
    val projectHash = project.rootProject.hashCode()

    @Input
    var moduleTypes: List<String> = listOf("core", "plugins")

    @OutputFile
    lateinit var dotFile: File

    @OutputFile
    lateinit var pngFile: File

    private
    fun Project.moduleType() = when {
        plugins.hasPlugin(gradlebuild.distribution.CorePlugin::class) -> "core"
        plugins.hasPlugin(gradlebuild.distribution.PluginsPlugin::class) -> "plugins"
        else -> "internal"
    }

    private
    fun color(project: Project) = when  {
        // "gray95"
        // "chartreuse3"
        declaredDistributions["Core"]!!.contains(project) -> "azure3"
        declaredDistributions["Basics"]!!.contains(project) -> "deepskyblue1"
        declaredDistributions["Publishing"]!!.contains(project) -> "darkolivegreen"
        declaredDistributions["Jvm"]!!.contains(project) -> "orange"
        declaredDistributions["Native"]!!.contains(project) -> "yellow"
        declaredDistributions["Full"]!!.contains(project) -> "orchid"
        declaredDistributions["Core"]!!.any { allDependenciesFor(it).contains(project.name) } -> "gray95"
        declaredDistributions["Basics"]!!.any { allDependenciesFor(it).contains(project.name) } -> "lightblue"
        declaredDistributions["Publishing"]!!.any { allDependenciesFor(it).contains(project.name) } -> "darkseagreen"
        declaredDistributions["Jvm"]!!.any { allDependenciesFor(it).contains(project.name) } -> "wheat"
        declaredDistributions["Native"]!!.any { allDependenciesFor(it).contains(project.name) } -> "khaki"
        declaredDistributions["Full"]!!.any { allDependenciesFor(it).contains(project.name) } -> "salmon"
        else -> "gray52" // portal plugins
    }

    private
    fun File.writeDotFile(content: String) = writeText("""
        digraph build_structure {
            graph [ dpi = 100, fontname="Sans"];
            node [fontname = "Sans"];
            edge [fontname = "Sans"];

            $content
        }
    """)

    private
    fun File.generateFrom(dotFile: File) = project.exec {
        commandLine("dot", "-Tpng", dotFile.path, "-o", path)
    }

    private
    fun addNode(name: String, color: String) =
        "\"$name\" [shape=\"box\", label=<<B>$name</B>>, color=\"$color\", bgcolor=\"$color\", style=\"filled\"]\n"

    private
    fun addEdge(from: String, to: String, color: String) =
        "\"$from\" -> \"$to\" [color=\"$color\"]\n"

    @TaskAction
    fun generate() {
        var content = ""


        listOf("Core", "Basics", "Publishing","Jvm", "Native", "Full").forEach { dist ->
            declaredDistributions[dist] =
                project.project(":distributions$dist").configurations["coreRuntimeOnly"].dependencies.map { (it as ProjectDependency).dependencyProject } +
                    project.project(":distributions$dist").configurations["pluginsRuntimeOnly"].dependencies.map { (it as ProjectDependency).dependencyProject }
        }

        with(project.rootProject) {
            val projectsByType = subprojects.filter { it.moduleType() in moduleTypes }.groupBy { it.moduleType() }
            projectsByType.forEach { (type, projects) ->
                content += "subgraph cluster_${type.toLowerCase()} {\n"
                //content += "label=<<B>${type.name}</B>>\n"
                content += "color=blue\n"
                content += "${type}\n"
                projects.forEach { project ->
                    val color = color(project)
                    content += addNode(project.name, color)
                }
                content += "}\n"
            }
            projectsByType.forEach { (_, projects) ->
                projects.forEach { project ->
                    project.configurations.filter { it.name in listOf("runtime", "compile", "implementation", "api", "compileOnly", "runtimeOnly") }.forEach { config ->
                        val edgeColor = if (config.name == "runtime") "firebrick1" else if (config.name == "compile") "deeppink" else "black"
                        config.dependencies.forEach { dep ->
                            var transitve = false
                            if (dep is ProjectDependency) {
                                val depName = dep.dependencyProject.name
                                config.dependencies.forEach { other ->
                                    if (other is ProjectDependency) {
                                        val otherName = other.dependencyProject.name
                                        if (otherName != depName && allDependenciesFor(other.dependencyProject).contains(depName)) {
                                            transitve = true
                                        }
                                    }
                                }
                                if (!transitve && dep.dependencyProject.moduleType() in moduleTypes) {
                                    content += addEdge(project.name, depName, edgeColor)
                                }
                            }
                        }
                    }
                }
            }
        }

        dotFile.writeDotFile(content)
        pngFile.generateFrom(dotFile)
    }

    private fun allDependenciesFor(project: Project): Set<String> =
        allDependencies.getOrPut(project.name) {
            val deps = mutableSetOf<String>()
            project.configurations.filter { it.name in listOf("runtime", "compile", "implementation", "api", "compileOnly", "runtimeOnly") }.forEach { config ->
                config.dependencies.forEach { dep ->
                    if (dep is ProjectDependency && !dep.dependencyProject.name.startsWith("distributions")) {
                        deps.add(dep.dependencyProject.name)
                        deps.addAll(allDependenciesFor(dep.dependencyProject))
                    }
                }
            }
            deps
        }
}
*/
