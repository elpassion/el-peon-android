package pl.elpassion.elspace

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import pl.elpassion.BuildConfig
import pl.elpassion.elspace.common.ContextProvider

class ElSpaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics.Builder().core(createCrashlyticsCore()).build())
        ContextProvider.override = { applicationContext }
    }

    private fun createCrashlyticsCore() = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
}