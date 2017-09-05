package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.logged_user_comment.view.*
import pl.elpassion.elspace.common.extensions.formatMillisToTime
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentStatus

class LoggedUserCommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.run {
            loggedUserCommentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
            loggedUserCommentInitials.text = item.userInitials
            loggedUserCommentName.text = item.fullName
            loggedUserCommentMessage.text = item.content
            loggedUserCommentTime.text = item.createdAt.formatMillisToTime()
            when (item.commentStatus) {
                CommentStatus.PENDING -> loggedUserCommentPendingInfo.show()
                else -> loggedUserCommentPendingInfo.hide()
            }
        }
    }
}