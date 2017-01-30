package pl.elpassion.elspace.hub.report.list

import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.CachedProjectRepository
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Completable
import rx.Observable

class ReportListActivityEditReportTest {

    private val service = mock<ReportList.Service>()
    private val editReportApi = mock<ReportEdit.EditApi>().apply { whenever(editReport(any(), any(), any(), any(), any())).thenReturn(Completable.complete()) }

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        ReportEdit.EditApiProvider.override = { editReportApi }
        CachedProjectRepositoryProvider.override = { mock<CachedProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
        stubCurrentTime(year = 2016, month = 10, day = 1)
        whenever(service.getReports())
                .thenReturn(Observable.just(listOf(newRegularHourlyReport(year = 2016, month = 10, day = 1, project = newProject(name = "Project"), description = "Description", reportedHours = 8.0))))
                .thenReturn(Observable.just(listOf(newRegularHourlyReport(year = 2016, month = 10, day = 1, project = newProject(name = "Project"), description = "new Description", reportedHours = 8.0))))
        ReportList.ServiceProvider.override = { service }
    }

    @Test
    fun shouldCloseEditReportActivityAndMakeSecondCallToUpdateReports() {
        onText("Description").click()
        onId(R.id.reportEditDescription).perform(clearText(), replaceText("new Description"), closeSoftKeyboard())
        onId(R.id.reportEditSaveButton).click()
        onId(R.id.reportsContainer).check(matches(hasDescendant(withText("new Description"))))
    }
}
