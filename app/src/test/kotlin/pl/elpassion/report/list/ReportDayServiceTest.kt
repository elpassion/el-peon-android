package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.DateChangeObserver
import pl.elpassion.report.list.service.ReportDayServiceImpl
import rx.Observable
import java.util.*

class ReportDayServiceTest {
    val dateChangeObserver = mock<DateChangeObserver>()
    val serviceApi = mock<ReportList.Service>()
    var service = ReportDayServiceImpl(dateChangeObserver, serviceApi)

    @Test
    fun shouldCreate31DaysWithoutReportsIfIsOctoberAndApiReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 10,
                daysInMonth = 31)
    }

    @Test
    fun shouldCreate30DaysWithoutReportsIfIsNovemberAndApiReturnsEmptyList() {
        verifyIfMapCorrectListForGivenParams(
                apiReturnValue = emptyList(),
                month = 11,
                daysInMonth = 30
        )
    }

    @Test
    fun shouldCorrectlyMapDayName() {
        stubDateChangeObserver(year = 2016, month = 9)
        stubServiceToReturn(emptyList())

        assertEquals(getFirstDay().name, "1 Thu")
    }

    @Test
    fun shouldReallyCorrectlyMapDayName() {
        stubDateChangeObserver(year = 2016, month = 9)
        stubServiceToReturn(emptyList())

        assertEquals(getDays()[1].name, "2 Fri")
    }

    @Test
    fun shouldMarkUnreportedPassedDays() {
        stubDateChangeObserver(year = 2016, month = 6)
        stubServiceToReturn(emptyList())

        assertTrue(getFirstDay().hasPassed)
    }

    @Test
    fun shouldMapReturnedReportsToCorrectDays() {
        val report = newReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        assertEquals(getFirstDay().reports, listOf(report))
    }

    @Test
    fun shouldCallDataChangeObserverSetNextMonth() {
        service.changeMonthToNext()

        verify(dateChangeObserver).setNextMonth()
    }

    @Test
    fun shouldCallDataChangeObserverSetPreviousMonth() {
        service.changeMonthToPrevious()

        verify(dateChangeObserver).setPreviousMonth()
    }

    private fun getDays() = service.createDays().toBlocking().first()

    private fun getFirstDay() = getDays().first()

    private fun stubServiceToReturn(list: List<Report>) {
        whenever(serviceApi.getReports()).thenReturn(Observable.just(list))
    }

    private fun stubDateChangeObserver(year: Int, month: Int, day: Int = 1) {
        stubCurrentTime(year = year, month = month, day = day)
        val initialDateCalendar: Calendar = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
        whenever(dateChangeObserver.observe()).thenReturn(Observable.just(initialDateCalendar.toYearMonth()))
    }

    private fun verifyIfMapCorrectListForGivenParams(apiReturnValue: List<Report>, daysInMonth: Int, month: Int) {
        stubServiceToReturn(apiReturnValue)
        stubDateChangeObserver(year = 2016, month = month)
        assertEquals(getDays().size, daysInMonth)
    }

}