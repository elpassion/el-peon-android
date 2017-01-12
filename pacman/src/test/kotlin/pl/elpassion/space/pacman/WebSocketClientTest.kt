package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import rx.observers.TestSubscriber

class WebSocketClientTest {

    val subscriber = TestSubscriber<String>()
    val api = mock<WebSocketClient.Api>()
    val client = WebSocketClient(api, "")

    @Test
    fun shouldConnectToApi() {
        client.connect().subscribe()
        verify(api).connect(any(), any())
    }
}