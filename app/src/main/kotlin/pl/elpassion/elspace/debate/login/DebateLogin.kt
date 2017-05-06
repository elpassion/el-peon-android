package pl.elpassion.elspace.debate.login

import io.reactivex.Observable
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.common.Provider
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DebateLogin {
    interface View {
        fun openDebateScreen(authToken: String)
        fun showLoginFailedError()
        fun showLoader()
        fun hideLoader()
        fun showWrongPinError()
    }

    interface Api {
        @FormUrlEncoded
        @POST("login")
        fun login(@Field("code") code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

    object ApiProvider: Provider<Api>({
        DebateRetrofitProvider.get().create(Api::class.java)
    })

}