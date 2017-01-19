package pl.elpassion.report.list

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onId
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.hasChildWithText
import pl.elpassion.common.hasNoChildWithText
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.report.Report
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.startActivity
import rx.Observable

class ReportListActivityScrollToTodayTest {

    val service = mock<ReportList.Service>()

    @JvmField @Rule
    val rule = rule<ReportListActivity>(autoStart = false)

    @Test
    fun shouldScrollToTodayOnTodayClickWhenNoReports() {
        stubServiceAndStart(reports = emptyList(), todayDate = "2017-01-31")
        onId(R.id.action_today).click()
        onId(R.id.reportsContainer).hasChildWithText("31 Tue")
    }

    @Test
    fun shouldReallyScrollToTodayOnTodayClickWhenNoReports() {
        stubServiceAndStart(reports = emptyList(), todayDate = "2017-01-20")
        onId(R.id.action_today).click()
        onId(R.id.reportsContainer).hasChildWithText("20 Fri")
        onId(R.id.reportsContainer).hasNoChildWithText("31 Tue")
    }

    private fun stubServiceAndStart(reports: List<Report>, todayDate: String) {
        stubCurrentTime(todayDate)
        whenever(service.getReports()).thenReturn(Observable.just(reports))
        ReportList.ServiceProvider.override = { service }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), todayDate))
    }
}