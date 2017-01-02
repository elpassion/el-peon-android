package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.project.Project
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.project.last.LastSelectedProjectRepositoryProvider
import pl.elpassion.report.datechooser.showDateDialog

class ReportAddActivity : AppCompatActivity(), ReportAdd.View {

    private val controller by lazy {
        ReportAddController(intent.getStringExtra(ADD_DATE_KEY), this, LastSelectedProjectRepositoryProvider.get(), ReportAdd.ApiProvider.get())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        showBackArrowOnActionBar()
        controller.onCreate()
        reportAddProjectName.setOnClickListener { controller.onProjectClicked() }
        reportAddHours.setOnTouchListener { view, motionEvent -> reportAddHours.text = null; false }
        reportAddAdd.setOnClickListener {
            controller.onReportAdd(
                    reportAddHours.text.toString(),
                    reportAddDescription.text.toString()
            )
        }
        reportAddHours.setOnTouchListener { view, motionEvent -> reportAddHours.text = null; false }
        reportAddDate.setOnClickListener { showDateDialog(supportFragmentManager, { controller.onDateSelect(it) }) }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun showDate(date: String) {
        reportAddDate.text = date
    }

    override fun showSelectedProject(project: Project) {
        reportAddProjectName.text = project.name
    }

    override fun enableAddReportButton() {
        reportAddAdd.isEnabled = true
    }

    override fun openProjectChooser() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun showLoader() = showLoader(reportAddCoordinator)

    override fun hideLoader() = hideLoader(reportAddCoordinator)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.onSelectProject(ProjectChooseActivity.getProject(data!!))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleClickOnBackArrowItem(item)

    override fun close() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportAddCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showEmptyDescriptionError() {
        Snackbar.make(reportAddCoordinator, R.string.empty_description_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun hideHoursInput() {

    }

    override fun showHoursInput() {

    }

    override fun showRegularReportDetails() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showPaidVacationsReportDetails() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSickLeaveReportDetails() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showUnpaidVacationsReportDetails() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val REQUEST_CODE = 10001
        private val ADD_DATE_KEY = "dateKey"

        fun startForResult(activity: Activity, date: String, requestCode: Int) {
            activity.startActivityForResult(intent(activity, date), requestCode)
        }

        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(intent(activity), requestCode)
        }

        fun intent(context: Context) = Intent(context, ReportAddActivity::class.java)

        fun intent(context: Context, date: String) = intent(context).apply { putExtra(ADD_DATE_KEY, date) }

    }
}