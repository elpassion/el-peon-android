package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.AdapterItem

class SeparatorItemViewHolder(itemView: View) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {}

    companion object {
        fun create() = R.layout.hub_separator to ::SeparatorItemViewHolder
    }
}