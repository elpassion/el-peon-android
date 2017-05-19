package pl.elpassion.elspace.debate.login

import com.elpassion.android.commons.espresso.doesNotExist
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.common.onToolbarBackArrow
import pl.elpassion.elspace.common.rule

class DebateLoginActivityWithoutLauncherTest {

    @JvmField @Rule
    val rule = rule<DebateLoginActivity>()

    @Test
    fun shouldShowToolbarWithoutBackArrow() {
        onToolbarBackArrow().doesNotExist()
    }
}