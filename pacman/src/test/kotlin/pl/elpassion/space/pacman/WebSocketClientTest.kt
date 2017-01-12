package pl.elpassion.space.pacman

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocketListener
import org.junit.Assert.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber
import kotlin.properties.Delegates

class WebSocketClientTest {

    val subscriber = TestSubscriber<WebSocketClient.Event>()
    val api = mock<WebSocketClient.Api>()

    @Test
    fun shouldConnectToApi() {
        val client = WebSocketClient(api, "")
        client.connect().subscribe()
        verify(api).connect(any(), any())
    }

    @Test
    fun shouldEmitOpenedEventOnOpen() {
        val stub = ApiStub()
        val client = WebSocketClient(stub, "")
        client.connect().subscribe(subscriber)
        stub.listener.onOpen(mock(), createResponseStub())
        subscriber.assertValueThat { it is WebSocketClient.Event.Opened }
    }

    @Test
    fun shouldEmitEventOnApiFailure() {
        val stub = ApiStub()
        val client = WebSocketClient(stub, "")
        client.connect().subscribe(subscriber)
        stub.listener.onFailure(mock(), null, createResponseStub())
        subscriber.assertValueCount(1)
    }

    private fun createResponseStub() = Response.Builder()
            .request(createRequestStub())
            .protocol(Protocol.HTTP_2)
            .code(200)
            .build()

    private fun createRequestStub() = Request.Builder()
            .url("ws://192.168.1.19:8080/ws")
            .build()

    class ApiStub : WebSocketClient.Api {

        var listener : WebSocketListener by Delegates.notNull<WebSocketListener>()

        override fun connect(url: String, listener: WebSocketListener) {
            this.listener = listener
        }
    }

    fun <T> TestSubscriber<T>.assertValueThat(predicate: (T) -> Boolean) {
        val events = onNextEvents
        assertEquals(events.size, 1)
        assert(predicate(events.first()))
    }

    fun <T> TestSubscriber<T>.assertLastValueThat(predicate: (T) -> Boolean) = assert(predicate(onNextEvents.last()))
}