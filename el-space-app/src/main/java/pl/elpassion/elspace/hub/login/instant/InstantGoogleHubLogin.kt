package pl.elpassion.elspace.hub.login.instant

import io.reactivex.Single

interface InstantGoogleHubLogin {
    interface View {
        fun openOnLoggedInScreen()
        fun startGoogleLoginIntent()
        fun showGoogleLoginError()
    }

    interface Repository {
        fun readToken(): String?
    }

    data class HubGoogleSignInResult(
            val isSuccess: Boolean,
            val googleToken: String?)

    interface Api {
        fun loginWithGoogle(googleToken: String): Single<String>
    }
}