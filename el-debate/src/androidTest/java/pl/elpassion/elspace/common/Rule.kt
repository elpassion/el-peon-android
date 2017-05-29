package pl.elpassion.elspace.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
            Animations.areEnabled = false
            return super.apply(base, description)
        }

        override fun beforeActivityLaunched() {
            beforeActivity.invoke()
        }
    }
}