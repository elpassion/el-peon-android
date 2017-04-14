package pl.elpassion.elspace.debate.login

import pl.elpassion.elspace.api.RetrofitProvider
import pl.elpassion.elspace.common.Provider
import rx.Observable

interface DebateLogin {
    interface View {
        fun openDebateScreen(authToken: String)
        fun showLoginFailedError()
        fun showLoader()
        fun hideLoader()
        fun showWrongPinError()
    }

    interface Api {
        fun login(code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

    object ApiProvider: Provider<Api>({
        RetrofitProvider.get().create(Api::class.java)
    })

}