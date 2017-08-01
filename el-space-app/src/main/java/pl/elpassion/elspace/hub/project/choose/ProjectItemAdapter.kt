package pl.elpassion.elspace.hub.project.choose

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.project_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.project.Project

class ProjectItemAdapter(val project: Project, val onClickListener: () -> Unit) : StableItemAdapter<ProjectItemAdapter.Holder>(project.id, R.layout.project_item) {

    override fun onCreateViewHolder(itemView: View) = Holder(itemView)

    override fun onBindViewHolder(holder: Holder) {
        holder.itemView.projectName.text = project.name
        holder.itemView.setOnClickListener { onClickListener() }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}