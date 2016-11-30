package pl.elpassion.report.edit

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import rx.Observable

class ReportEditControllerTest {

    private val view = mock<ReportEdit.View>()
    private val editReportApi = mock<ReportEdit.EditApi>()
    private val controller = ReportEditController(view, editReportApi)

    @Before
    fun setUp() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.just(null))
    }

    @Test
    fun shouldShowCorrectReportOnCreate() {
        val report = newReport()
        controller.onCreate(report)
        verify(view, times(1)).showReport(report)
    }

    @Test
    fun shouldOpenChooseProjectScreenOnChooseProject() {
        controller.onChooseProject()
        verify(view, times(1)).openChooseProjectScreen()
    }

    @Test
    fun shouldCallApiWithCorrectDataOnSaveReport() {
        controller.onCreate(newReport(year = 2017, month = 7, day = 2, id = 2, description = "DESCRIPTION", reportedHours = 4.0, projectId = 2))
        controller.onSaveReport(hours = "8.0", description = "description")
        verify(editReportApi, times(1)).editReport(id = 2, date = "2017-07-02", reportedHour = "8.0", description = "description", projectId = "2")
    }

    @Test
    fun shouldReallyCallApiWithCorrectDataOnSaveReport() {
        controller.onCreate(newReport(year = 2016, month = 1, day = 3, id = 5, description = "DESCRIPTION", reportedHours = 4.0, projectId = 2))
        controller.onSaveReport(hours = "7.5", description = "newDescription")
        verify(editReportApi, times(1)).editReport(id = 5, date = "2016-01-03", reportedHour = "7.5", description = "newDescription", projectId = "2")
    }

    @Test
    fun shouldCallApiWithCorrectProjectIdIfItHasBeenChanged() {
        controller.onCreate(newReport(projectId = 10))
        controller.onSelectProject(newProject(id = "20"))
        controller.onSaveReport("0.0", "")
        verify(editReportApi, times(1)).editReport(any(), any(), any(), any(), projectId = eq("20"))
    }

    @Test
    fun shouldUpdateProjectNameOnNewProject() {
        controller.onSelectProject(newProject(name = "newProject"))
        verify(view, times(1)).updateProjectName(projectName = "newProject")
    }

    @Test
    fun shouldShowLoaderOnSaveReport() {
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        verify(view, times(1)).showLoader()
    }

    @Test
    fun shouldHideLoaderOnSaveReportFinish() {
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldNotHideLoaderIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        verify(view, never()).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroyIfSavingHasNotFinished() {
        stubEditReportApiToReturnNever()
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        controller.onDestroy()
        verify(view, times(1)).hideLoader()
    }

    @Test
    fun shouldShowErrorWhenSavingReportFails() {
        stubEditReportApiToReturnError()
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        verify(view, times(1)).showError(any())
    }

    @Test
    fun shouldCloseViewWhenSavingHasNotFailed() {
        controller.onCreate(newReport())
        controller.onSaveReport("1.0", "")
        verify(view, times(1)).close()
    }

    private fun stubEditReportApiToReturnNever() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.never())
    }

    private fun stubEditReportApiToReturnError() {
        whenever(editReportApi.editReport(any(), any(), any(), any(), any())).thenReturn(Observable.error(RuntimeException()))
    }

}