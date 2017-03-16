package pl.elpassion.elspace.hub.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.Report

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

    @Test
    fun shouldShowReportDate() {
        stubReportAndStart(newDailyReport(year = 2016, month = 10, day = 1))
        onId(R.id.reportEditDate).hasText("2016-10-01")
    }

    @Test
    fun shouldShowReportedHoursForHourlyReport() {
        stubReportAndStart(newRegularHourlyReport(reportedHours = 6.5))
        onId(R.id.reportEditHours).hasText("6.5")
    }

    @Test
    fun shouldShowReportedHoursForHourlyReportWithoutTrailingZeroes() {
        stubReportAndStart(newRegularHourlyReport(reportedHours = 6.0))
        onId(R.id.reportEditHours).hasText("6")
    }

    @Test
    fun shouldShowProjectNameForRegularReport() {
        stubReportAndStart(newRegularHourlyReport(project = newProject(name = "Slack Time")))
        onId(R.id.reportEditProjectName).hasText("Slack Time")
    }

    @Test
    fun shouldShowDescriptionForRegularReport() {
        stubReportAndStart(newRegularHourlyReport(description = "EL Space"))
        onId(R.id.reportEditDescription).hasText("EL Space")
    }
    
    @Test
    fun shouldShowAdditionalInfoForSickLeave() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.SICK_LEAVE))
        onId(R.id.reportEditAdditionalInfo).hasText(R.string.report_add_sick_leave_info)
    }

    private fun stubReportAndStart(report: Report = newRegularHourlyReport()) {
        rule.startActivity(ReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}