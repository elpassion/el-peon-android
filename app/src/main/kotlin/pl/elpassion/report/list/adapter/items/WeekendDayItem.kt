package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_ID
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.list.Day
import pl.elpassion.report.list.OnDayClickListener

class WeekendDayItem(val day: Day, val listener: OnDayClickListener) : StableItemAdapter<WeekendDayItem.VH>(NO_ID, R.layout.weekend_day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDayDate(day.date) }
        holder.itemView.dayNumber.text = day.name
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}