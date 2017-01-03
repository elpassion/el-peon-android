package pl.elpassion.report.add.details

import pl.elpassion.project.Project
import pl.elpassion.project.last.LastSelectedProjectRepository

class ReportAddDetailsRegularController(private val view: ReportAddDetails.View.Regular,
                                        private val sender: ReportAddDetails.Sender.Regular,
                                        private val repository: LastSelectedProjectRepository) : ReportAddDetails.Controller {

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
    }

    fun onProjectClicked() {
        view.openProjectChooser()
    }

    fun onSelectProject(newProject: Project) {
        view.showSelectedProject(newProject)
    }

    override fun onReportAdded() {
        if (view.getDescription().isNotBlank()) {
            sender.sendAddReport(view.getDescription(), view.getHours())
        } else {
            view.showEmptyDescriptionError()
        }
    }
}