package com.github.spragucm.intellijkotlindsluidatabinding.services

import com.intellij.openapi.project.Project
import com.github.spragucm.intellijkotlindsluidatabinding.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
