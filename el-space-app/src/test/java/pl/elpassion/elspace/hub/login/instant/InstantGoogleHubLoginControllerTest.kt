package pl.elpassion.elspace.hub.login.instant

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class InstantGoogleHubLoginControllerTest {

    val view = mock<InstantGoogleHubLogin.View>()
    val repository = mock<InstantGoogleHubLogin.Repository>()
    val controller = InstantGoogleHubLoginController(view, repository)

    @Test
    fun shouldOpenOnLoggedInScreenIfUserIsLoggedInOnCreate() {
        stubRepositoryToReturn("token")
        controller.onCreate()
        verify(view).openOnLoggedInScreen()
    }

    @Test
    fun shouldNotOpenOnLoggedInScreenIfUserIsNotLoggedInOnCreate() {
        stubRepositoryToReturn(null)
        controller.onCreate()
        verify(view, never()).openOnLoggedInScreen()
    }

    private fun stubRepositoryToReturn(token: String?) {
        whenever(repository.readToken()).thenReturn(token)
    }
}
