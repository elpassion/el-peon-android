package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.DeaultMocksRule
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ReportAddActivityChoosingProjectTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val defaultMocks = DeaultMocksRule()

    @JvmField @Rule
    val rule = ActivityTestRule<ReportAddActivity>(ReportAddActivity::class.java, false, false)

    @Test
    fun shouldChangeSelectedProject() {
        stubRepositoryAdnStart(listOf(newProject(), newProject("id2", "name2")))
        onId(R.id.reportAddProjectName).click()
        onText("name2").click()
        onId(R.id.reportAddProjectName).hasText("name2")
    }

    private fun stubRepositoryAdnStart(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        ProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), "2016-01-01"))
    }
}

