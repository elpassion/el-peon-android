package pl.elpassion.elspace.hub.report.edit.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.edit.ReportEdit

class ReportEditServiceTest {

    val api = mock<ReportEdit.EditApi>()
    val service = ReportEditServiceImpl(api)

    @Test
    fun shouldCallApiWithProperDataOfPaidVacationReport() {
        val report = newPaidVacationHourlyReport(day = 1, month = 1, year = 2016, reportedHours = 8.0)
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = "", projectId = null, reportedHour = "8.0")
    }

    @Test
    fun shouldReallyCallApiWithProperDataOfPaidVacationReport() {
        val report = newPaidVacationHourlyReport(day = 2, month = 2, year = 2015, reportedHours = 7.0)
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = "", projectId = null, reportedHour = "7.0")
    }

    @Test
    fun shouldCallApiWithProperDataOfRegularHourlyReport() {
        val report = newRegularHourlyReport(day = 1, month = 1, year = 2016, reportedHours = 8.0, description = "123", project = newProject(id = 1))
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = report.description, projectId = report.project.id, reportedHour = "8.0")
    }

    @Test
    fun shouldReallyCallApiWithProperDataOfRegularHourlyReport() {
        val report = newRegularHourlyReport(day = 2, month = 2, year = 2016, reportedHours = 7.0, description = "234", project = newProject(id = 2))
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = report.description, projectId = report.project.id, reportedHour = "7.0")
    }

    @Test
    fun shouldCallApiWithProperDataOfDailyReport() {
        val report = newDailyReport(day = 1, month = 1, year = 2016, reportType = DailyReportType.SICK_LEAVE)
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = "SickLeave", projectId = null, reportedHour = "0")
    }

    @Test
    fun shouldCallApiWithReallyProperDataOfDailyReport() {
        val report = newDailyReport(day = 1, month = 1, year = 2016, reportType = DailyReportType.UNPAID_VACATIONS)
        service.edit(report)

        verify(api).editReport(id = report.id, date = report.date, description = "UnpaidVacation", projectId = null, reportedHour = "0")
    }
}