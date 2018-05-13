package retrofit2.dsl

import com.google.gson.JsonSyntaxException
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.converter.gson.GsonConverterFactory

class LazyDslCallbackTest {

    @get:Rule val server = MockWebServer()

    lateinit var service: MessagingService

    @Before
    fun setUp() {
        service = retrofitFor<MessagingService> {
            baseUrl(server.url(""))
            addConverterFactory(GsonConverterFactory.create())
        }
    }

    @Test
    fun then() {
        server.enqueue {
            setResponseCode(200)
            setBody("""{"message": "Hello"}""")
        }

        waitFor {
            service.getMessage().then {
                assert(it?.message == "Hello")
                resume()
            }
        }
    }

    @Test
    fun catchError() {
        server.enqueue {
            setResponseCode(400)
            setBody("""{"message": "Bad Request","code": 400}""")
        }

        waitFor {
            service.getMessage().then {}.catch { res, _ ->
                assert(res?.code() == 400)
                assert(res?.errorBody()?.string()?.contains("Bad Request") == true)
                resume()
            }
        }
    }

    @Test
    fun catchFailure() {
        server.enqueue {
            setResponseCode(200)
            setBody(""""message": "Hello"""")
        }

        waitFor {
            service.getMessage().then {}.catch { _, t ->
                assert(t is JsonSyntaxException)
                resume()
            }
        }
    }

    @Test
    fun finally() {
        server.enqueue {
            setResponseCode(200)
            setBody("""{"message": "Hello"}""")
        }

        waitFor {
            service.getMessage().then {
                assert(it?.message == "Hello")
            }.finally {
                resume()
            }
        }
    }
}
