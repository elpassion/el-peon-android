package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorTest {

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWithoutReports()))
        assertTrue(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithHourlyReports()))
        assertTrue(givenItems[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenAdapters = addSeparators(listOf(newRegularHourlyReport(), newDayWithoutReports()))
        assertTrue(givenAdapters[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenDayWithDailyReportAndNotFilledInDay() {
        val givenAdapters = addSeparators(listOf(newDayWithDailyReports(), newDayWithoutReports()))
        assertTrue(givenAdapters[1] is Separator)
    }

    @Test
    fun shouldHaveSeparatorBetweenDaysWithoutReports() {
        val givenAdapters = addSeparators(listOf(newDayWithoutReports(), newDayWithoutReports()))
        assertTrue(givenAdapters[1] is Separator)
    }

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, false)

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())
}