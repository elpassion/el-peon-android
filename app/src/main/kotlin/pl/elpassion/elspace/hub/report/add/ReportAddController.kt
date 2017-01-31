package pl.elpassion.elspace.hub.report.add

import pl.elpassion.elspace.api.applySchedulers

import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

class ReportAddController(private val date: String?,
                          private val view: ReportAdd.View,
                          private val api: ReportAdd.Api,
                          private val repository: LastSelectedProjectRepository) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        repository.getLastProject()?.let {
            view.showSelectedProject(it)
        }
        view.showDate(date ?: getCurrentDatePerformedAtString())
        Observable.merge(projectClickEvents(), addReportClicks(), reportTypeChanges())
                .subscribe()
                .save()
    }

    private fun getCurrentDatePerformedAtString() = getTimeFrom(timeInMillis = CurrentTimeProvider.get()).getDateString()

    private fun projectClickEvents() = view.projectClickEvents()
            .doOnNext { view.openProjectChooser() }

    private fun addReportClicks() = view.addReportClicks()
            .switchMap { handleNewReport(it) }
            .doOnError { view.showError(it) }
            .onErrorResumeNext { Observable.empty() }

    private fun reportTypeChanges() = view.reportTypeChanges()
            .doOnNext { onReportTypeChanged(it) }
            .map { Unit }

    private fun handleNewReport(reportViewModel: ReportViewModel) = when (reportViewModel) {
        is RegularReport -> regularReportHandler.invoke(reportViewModel)
        is UnpaidVacationsReport -> unpaidVacationReportHandler.invoke(reportViewModel)
        is PaidVacationsReport -> paidVacationReportHandler.invoke(reportViewModel)
        is SickLeaveReport -> sickLeaveReportHandler.invoke(reportViewModel)
        else -> Observable.error(IllegalArgumentException(reportViewModel.toString()))
    }

    private val regularReportHandler = { regularReport: RegularReport ->
        Observable.merge(emptyDescriptionErrorFlow(regularReport), emptyProjectErrorFlow(regularReport), validReportFlow(regularReport))
    }

    private val unpaidVacationReportHandler = { unpaidVacationsReport: UnpaidVacationsReport ->
        api.addUnpaidVacationsReport(unpaidVacationsReport.selectedDate)
                .toObservable<Unit>()
                .applySchedulers()
                .addLoader()
                .doOnCompleted { view.close() }
    }

    private val paidVacationReportHandler = { paidVacationsReport: PaidVacationsReport ->
        api.addPaidVacationsReport(paidVacationsReport.selectedDate, paidVacationsReport.hours)
                .toObservable<Unit>()
                .applySchedulers()
                .addLoader()
                .doOnCompleted { view.close() }
    }

    private val sickLeaveReportHandler = { sickLeaveReport: SickLeaveReport ->
        api.addSickLeaveReport(sickLeaveReport.selectedDate)
                .toObservable<Unit>()
                .applySchedulers()
                .addLoader()
                .doOnCompleted { view.close() }
    }

    private fun onReportTypeChanged(reportType: ReportType) = when (reportType) {
        ReportType.REGULAR -> showRegularForm()
        ReportType.PAID_VACATIONS -> showPaidVacationsForm()
        ReportType.SICK_LEAVE -> showSickLeaveForm()
        ReportType.UNPAID_VACATIONS -> showUnpaidVacationsForm()
    }

    private fun emptyDescriptionErrorFlow(regularReport: RegularReport): Observable<Unit> = Observable.just(regularReport)
            .filter { !it.hasDescription() }
            .doOnNext { view.showEmptyDescriptionError() }
            .map { Unit }

    private fun emptyProjectErrorFlow(regularReport: RegularReport): Observable<Unit> = Observable.just(regularReport)
            .filter { !it.hasProject() }
            .doOnNext { view.showEmptyProjectError() }
            .map { Unit }

    private fun validReportFlow(regularReport: RegularReport) = Observable.just(regularReport)
            .filter { it.hasProject() && it.hasDescription() }
            .switchMap { addRegularReportObservable(it) }

    private fun addRegularReportObservable(regularReport: RegularReport) =
            api.addRegularReport(regularReport.selectedDate, regularReport.project!!.id, regularReport.hours, regularReport.description)
                    .toObservable<Unit>()
                    .applySchedulers()
                    .addLoader()
                    .doOnCompleted { view.close() }

    private fun RegularReport.hasDescription() = description.isNotBlank()

    private fun RegularReport.hasProject() = project != null

    private fun <T> Observable<T>.addLoader() = this
            .doOnSubscribe { view.showLoader() }
            .doOnUnsubscribe { view.hideLoader() }
            .doOnTerminate { view.hideLoader() }

    fun onDestroy() {
        subscriptions.clear()
    }

    private fun showUnpaidVacationsForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
        view.showUnpaidVacationsInfo()
    }

    private fun showSickLeaveForm() {
        view.hideHoursInput()
        view.hideDescriptionInput()
        view.hideProjectChooser()
        view.showSickLeaveInfo()
    }

    private fun showPaidVacationsForm() {
        view.showHoursInput()
        view.hideProjectChooser()
        view.hideDescriptionInput()
        view.hideAdditionalInfo()
    }

    private fun showRegularForm() {
        view.showDescriptionInput()
        view.showProjectChooser()
        view.showHoursInput()
        view.hideAdditionalInfo()
    }

    private fun Subscription.save() {
        subscriptions.add(this)
    }

    fun onDateChanged(date: String) {
        view.showDate(date)
    }
}
