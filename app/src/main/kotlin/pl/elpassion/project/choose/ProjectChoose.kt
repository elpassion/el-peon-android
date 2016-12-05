package pl.elpassion.project.choose

import pl.elpassion.project.Project

interface ProjectChoose {
    interface View {
        fun showPossibleProjects(projects: List<Project>)

        fun showFilteredProjects(projects: List<Project>)

        fun selectProject(project: Project)
    }
}
