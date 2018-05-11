package retrofit2.dsl

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueue(response: MockResponse.() -> Unit) {
    val mock = MockResponse().apply(response)
    enqueue(mock)
}
