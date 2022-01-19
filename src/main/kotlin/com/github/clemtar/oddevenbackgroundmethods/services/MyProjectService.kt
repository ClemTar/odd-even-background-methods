package com.github.clemtar.oddevenbackgroundmethods.services

import com.intellij.openapi.project.Project
import com.github.clemtar.oddevenbackgroundmethods.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
