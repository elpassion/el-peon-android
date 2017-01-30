package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import rx.Completable

interface ReportEdit {

    interface View {
        fun showLoader()
        fun hideLoader()
        fun showError(ex: Throwable)
        fun close()
        fun showDate(date: String)
    }

    interface Regular {
        interface View : ReportEdit.View {
            fun showReport(report: RegularHourlyReport)
            fun openChooseProjectScreen()
            fun updateProjectName(projectName: String)
            fun showEmptyDescriptionError()
        }

        interface Service {
            fun edit(report: RegularHourlyReport): Completable
        }
    }

    interface PaidVacation {
        interface View : ReportEdit.View {
            fun showReportHours(reportHours: Double)
        }

        interface Service {
            fun edit(report: PaidVacationHourlyReport): Completable
        }
    }

    interface Daily {
        interface View : ReportEdit.View

        interface Service {
            fun edit(report: DailyReport): Completable
        }
    }

    interface EditApi {
        @PATCH("activities/{id}")
        fun editReport(@Path("id") id: Long,
                       @Query("activity[performed_at]") date: String,
                       @Query("activity[value]") reportedHour: String,
                       @Query("activity[comment]") description: String,
                       @Query("activity[project_id]") projectId: Long?): Completable
    }

    object EditApiProvider : Provider<EditApi>({
        RetrofitProvider.get().create(EditApi::class.java)
    })

    interface RemoveApi {
        @DELETE("activities/{id}")
        fun removeReport(@Path("id") reportId: Long): Completable
    }

    object RemoveApiProvider : Provider<RemoveApi>({
        RetrofitProvider.get().create(RemoveApi::class.java)
    })
}