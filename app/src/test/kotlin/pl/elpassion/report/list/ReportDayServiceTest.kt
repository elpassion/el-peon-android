package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newHoursReport
import pl.elpassion.report.HoursReport
import pl.elpassion.report.list.service.ReportDayServiceImpl
import rx.Observable
import java.util.*

class ReportDayServiceTest {
    val serviceApi = mock<ReportList.Service>()
    var service = ReportDayServiceImpl(serviceApi)

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
        val report = newHoursReport(year = 2016, month = 6, day = 1)
        stubDateChangeObserver(year = 2016, month = 6, day = 1)
        stubServiceToReturn(listOf(report))

        assertEquals(getFirstDay().reports, listOf(report))
    }

    private fun getDays() = service.createDays(createYearMonthFromTimeProvider()).toBlocking().first()

    private fun getFirstDay() = getDays().first()

    private fun createYearMonthFromTimeProvider() =
            Observable.just(Calendar.getInstance().apply { timeInMillis = CurrentTimeProvider.get() }.toYearMonth())

    private fun stubServiceToReturn(list: List<HoursReport>) {
        whenever(serviceApi.getReports()).thenReturn(Observable.just(list))
    }

    private fun stubDateChangeObserver(year: Int, month: Int, day: Int = 1) {
        stubCurrentTime(year = year, month = month, day = day)
    }

    private fun verifyIfMapCorrectListForGivenParams(apiReturnValue: List<HoursReport>, daysInMonth: Int, month: Int) {
        stubServiceToReturn(apiReturnValue)
        stubDateChangeObserver(year = 2016, month = month)
        assertEquals(getDays().size, daysInMonth)
    }

}