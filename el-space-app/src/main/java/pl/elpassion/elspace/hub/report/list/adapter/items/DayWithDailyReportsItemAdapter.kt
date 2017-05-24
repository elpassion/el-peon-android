package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.list.DayWithDailyReport
import pl.elpassion.elspace.hub.report.list.OnReportClick

class DayWithDailyReportsItemAdapter(override val day: DayWithDailyReport, val onReportClick: OnReportClick) :
        StableItemAdapter<DayWithDailyReportsItemAdapter.VH>(day.uuid, R.layout.day_with_daily_report_item), DayItem {

    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.run {
            dayNumber.text = day.name
            totalHours.setText(createReportDescription())
            setOnClickListener { onReportClick(day.report) }
        }
    }

    private fun createReportDescription() = when (day.report.reportType) {
        DailyReportType.SICK_LEAVE -> R.string.report_sick_leave_title
        else -> R.string.report_unpaid_vacations_title
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}