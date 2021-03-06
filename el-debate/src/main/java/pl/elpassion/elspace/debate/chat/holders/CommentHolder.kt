package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.comment.view.*
import pl.elpassion.elspace.common.extensions.formatMillisToTime
import pl.elpassion.elspace.debate.chat.Comment
import java.util.*

class CommentHolder(itemView: View, private val timeZone: () -> TimeZone) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.run {
            commentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
            commentInitials.text = item.userInitials
            commentName.text = item.fullName
            commentMessage.text = item.content
            commentTime.text = item.createdAt.formatMillisToTime(timeZone)
        }
    }
}