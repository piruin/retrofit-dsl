package retrofit2.dsl

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

fun MockWebServer.enqueue(block: MockResponse.() -> Unit) {
    enqueue(MockResponse().apply(block))
}
