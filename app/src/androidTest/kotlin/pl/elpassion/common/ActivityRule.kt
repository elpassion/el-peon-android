package pl.elpassion.common

import android.app.Activity
import android.content.Intent
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.InitIntentsRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.NoAnimationTestRule
import java.util.*

inline fun <reified T : Activity> activityTestRule(autoStart: Boolean = true,
                                                   activityIntent: Intent? = null,
                                                   disableAnimation: Boolean,
                                                   noinline before: (() -> Unit) = {}) = {
    val customTestRules = createCustomTestRules(disableAnimation)
    RuleChainAdapter(activityTestRuleImpl<T>(activityIntent, autoStart, before), customTestRules)
}

fun createCustomTestRules(disableAnimation: Boolean): ArrayList<TestRule> {
    val customRules = ArrayList<TestRule>()
    customRules.add(InjectTestModulesRule({}))
    if (disableAnimation) {
        customRules.add(NoAnimationTestRule())
    }
    return customRules
}

inline fun <reified T : Activity> activityTestRuleImpl(activityIntent: Intent?, autoStart: Boolean, noinline before: () -> Unit): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {

        override fun beforeActivityLaunched() = before()

        override fun afterActivityLaunched() {

        }

        override fun getActivityIntent() = activityIntent ?: super.getActivityIntent()

    }
}


class RuleChainAdapter<out T : Activity>(private val activityTestRule: ActivityTestRule<T>,
                                         private val customRules: List<TestRule> = emptyList()) : TestRule {

    private val rules = buildRuleChain()

    private fun buildRuleChain(): RuleChain {
        val ruleChain = RuleChain.outerRule(InitIntentsRule())
        customRules.forEach {
            ruleChain.around(it)
        }
        ruleChain.around(activityTestRule)
        return ruleChain
    }

    override fun apply(base: Statement?, description: Description?): Statement =
            rules.apply(base, description)

    fun launchActivity(intent: Intent = Intent()): T = activityTestRule.launchActivity(intent)

    fun isActivityFinishing() = activityTestRule.activity.isFinishing

    val activity: Activity
        get() = activityTestRule.activity
}
