package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.report.DailyReport
import pl.elpassion.report.edit.daily.ReportEditDailyActivity
import pl.elpassion.startActivity

class ReportEditDailyActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditDailyActivity>(autoStart = false)

    @Test
    fun shouldHaveVisibleBackArrow() {
        startActivity()
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun shouldHavePerformedAtHeader() {
        startActivity()
        onText(R.string.report_edit_date_header).isDisplayed()
    }

    private fun startActivity(report: DailyReport = newDailyReport()) {
        rule.startActivity(ReportEditDailyActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}
