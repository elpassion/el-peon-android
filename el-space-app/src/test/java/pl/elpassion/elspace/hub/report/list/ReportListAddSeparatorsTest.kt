package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport


class ReportListAddSeparatorsTest {

    @Test
    fun shouldHaveEmptyOnFirstPosition() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems[0] is Empty)
    }

    @Test
    fun shouldHaveEmptyOnLastPosition() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems[2] is Empty)
    }

    @Test
    fun shouldHaveCorrectListSizeWithOneElement() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport()))
        assertTrue(givenItems.size == 3)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoReportItemAdapters() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertFalse(givenItems[2] is Separator)
    }

    @Test
    fun shouldHaveCorrectListSizeWithTwoElementsWithoutSeparator() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newRegularHourlyReport()))
        assertTrue(givenItems.size == 4)
    }

    @Test
    fun shouldHaveSeparatorBetweenNotFilledInDayAndFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithHourlyReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenFilledInDayAndNotFilledIn() {
        val givenItems = addSeparators(listOf(newDayWithHourlyReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithHourlyReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenDayWithDailyReportAndNotFilledInDay() {
        val givenItems = addSeparators(listOf(newDayWithDailyReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldHaveSeparatorBetweenDaysWithoutReports() {
        val givenItems = addSeparators(listOf(newDayWithoutReports(), newDayWithoutReports()))
        assertTrue(givenItems[2] is Separator)
        assertTrue(givenItems.size == 5)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenPaidVacationAndRegularHourlyReport() {
        val givenItems = addSeparators(listOf(newRegularHourlyReport(), newPaidVacationHourlyReport()))
        assertFalse(givenItems[2] is Separator)
        assertTrue(givenItems.size == 4)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenWeekendAndEmptyItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), Empty()))
        assertFalse(givenItems[2] is Separator)
        assertTrue(givenItems.size == 4)
    }

    @Test
    fun shouldNotHaveSeparatorBetweenTwoWeekendItems() {
        val givenItems = addSeparators(listOf(newDayWeekend(), newDayWeekend()))
        assertFalse(givenItems[2] is Separator)
        assertTrue(givenItems.size == 4)
    }

    private fun newDayWithoutReports() = DayWithoutReports(0, "", "", false, isWeekend = false)

    private fun newDayWithHourlyReports() = DayWithHourlyReports(0, "", "", listOf(newRegularHourlyReport()), false, 1.0)

    private fun newDayWithDailyReports() = DayWithDailyReport(0, "", "", false, newDailyReport())

    private fun newDayWeekend() = DayWithoutReports(0, "", "", false, isWeekend = true)
}