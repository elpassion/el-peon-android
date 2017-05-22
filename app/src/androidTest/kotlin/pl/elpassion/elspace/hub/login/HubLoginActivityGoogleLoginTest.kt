package pl.elpassion.elspace.hub.login

import android.support.test.espresso.Espresso
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.*
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class HubLoginActivityGoogleLoginTest {

    private val GOOGLE_TOKEN = "google token"

    private val hubLoginTokenApi = mock<HubLogin.TokenApi>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<HubLoginActivity> {
        wheneverLoginWithGoogleToken().thenReturn(Observable.just(HubTokenFromApi("token")))
        HubLoginRepositoryProvider.override = { mock<HubLogin.Repository>() }
        HubLoginTokenApiProvider.override = { hubLoginTokenApi }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Before
    fun setupTests() {
        stubGoogleSignIn()
        prepareAutoFinishingIntent()
        Espresso.closeSoftKeyboard()
    }

    private fun stubGoogleSignIn() {
        GoogleSingInDI.startGoogleSignInActivity = { activity, _, resultCode -> activity.startActivityForResult(getAutoFinishingIntent(), resultCode) }
        GoogleSingInDI.getELPGoogleSignInResultFromIntent = {
            object : ELPGoogleSignInResult {
                override val isSuccess = true
                override val idToken = GOOGLE_TOKEN
            }
        }
    }

    @Test
    fun shouldHaveGoogleSignInButton() {
        onId(R.id.hubLoginGoogleSignInButton).hasText(R.string.hub_login_button_google_sign_in)
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        onId(R.id.hubLoginGoogleSignInButton).click()
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowLoaderWhileSigningInWithGoogle() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.never())
        onId(R.id.hubLoginGoogleSignInButton).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldHideLoaderWhenSigningInWithGoogleFinished() {
        onId(R.id.hubLoginGoogleSignInButton).click()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorWhenGoogleAccessTokenFailed() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.error(RuntimeException()))
        onId(R.id.hubLoginGoogleSignInButton).click()
        onText(R.string.google_token_error).isDisplayed()
    }

    @Test
    fun shouldNotShowErrorWhenGoogleLoginCanceled() {
        prepareAutoCancelingIntent()
        onId(R.id.hubLoginGoogleSignInButton).click()
        onText(R.string.google_token_error).doesNotExist()
    }

    private fun wheneverLoginWithGoogleToken() =
            whenever(hubLoginTokenApi.loginWithGoogleToken(GoogleTokenForHubTokenApi(GOOGLE_TOKEN)))

}
