package retrofit2.dsl

import com.google.gson.JsonSyntaxException
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class DslCallbackTest {

    @get:Rule val server = MockWebServer()

    lateinit var service: MessagingService

    @Before fun setUp() {
        service = retrofitFor<MessagingService> {
            baseUrl(server.url(""))
            addConverterFactory(GsonConverterFactory.create())
        }
    }

    @Test fun onSuccess() {
        server.enqueue {
            setResponseCode(200)
            setBody("""{"message": "Hello"}""")
        }

        waiter {
            service.getMessage().enqueue {
                onSuccess {
                    assert(body()?.message == "Hello") { "Response must be Hello" }
                    resume()
                }
            }
        }
    }

    @Test fun onFailure() {
        server.enqueue {
            setResponseCode(200)
            setBody(""""message": "Hello"""")
        }

        waiter {
            service.getMessage().enqueue {
                onFailure {
                    assert(it is JsonSyntaxException)
                    resume()
                }
            }
        }
    }

    @Test fun onError() {
        server.enqueue {
            setResponseCode(400)
            setBody("""{"message": "Bad Request","code": 400}""")
        }
        waiter {
            service.getMessage().enqueue {
                onError {
                    assert(code() == 400)
                    assert(errorBody()?.string()?.contains("Bad Request") == true)
                    resume()
                }
            }
        }
    }

    interface MessagingService {
        @GET("message") fun getMessage(): Call<Messaging>
    }

    data class Messaging(val code: Int?, val message: String)
}
