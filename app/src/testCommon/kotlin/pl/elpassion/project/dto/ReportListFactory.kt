package pl.elpassion.project.dto

import pl.elpassion.project.Project
import pl.elpassion.report.DailyReport
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.HourlyReport
import pl.elpassion.report.HourlyReportType

fun newHourlyReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportedHours: Double = 4.0, project: Project? = newProject(id = 1, name = "Project"), description: String = "description", reportType: HourlyReportType = HourlyReportType.REGULAR) =
        HourlyReport(id = id, year = year, month = month, day = day, reportedHours = reportedHours, project = project, description = description, reportType = reportType)

fun newDailyReport(id: Long = 1, year: Int = 2016, month: Int = 6, day: Int = 1, reportType: DailyReportType = DailyReportType.SICK_LEAVE) =
        DailyReport(id = id, year = year, month = month, day = day, reportType = reportType)