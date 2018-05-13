package retrofit2.dsl

import com.google.gson.JsonSyntaxException
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.converter.gson.GsonConverterFactory

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

        waitFor {
            service.getMessage().enqueue {
                var isAlwaysCall = false
                always {
                    isAlwaysCall = true
                }
                onSuccess {
                    assert(body()?.message == "Hello") { "Response must be Hello" }
                }
                finally {
                    assert(isAlwaysCall)
                    resume()
                }
            }
        }
    }

    @Test fun onRedirect() {
        server.enqueue {
            setResponseCode(300)
            setBody("""{"message": "Redirect"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    assert(true == false) { "onSuccess shouldn't be call" }
                }
                onError {
                    assert(code() == 300) { "Must call onError before onClientError" }
                }
                onRedirect {
                    assert(errorBody()?.string()?.contains("Redirect") == true)
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

        waitFor {
            service.getMessage().enqueue {
                var isAlwaysCall = false
                always {
                    isAlwaysCall = true
                }
                onFailure {
                    assert(it is JsonSyntaxException)
                }
                finally {
                    assert(isAlwaysCall)
                    resume()
                }
            }
        }
    }

    @Test fun onNotSuccess() {
        server.enqueue {
            setResponseCode(400)
            setBody("""{"message": "Bad Request","code": 400}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onError {
                    assert(code() == 400)
                    assert(errorBody()?.string()?.contains("Bad Request") == true)
                    resume()
                }
            }
        }
    }

    @Test fun onClientError() {
        server.enqueue {
            setResponseCode(401)
            setBody("""{"message": "Unauthorized","code": 401}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onError {
                    assert(code() == 401) { "Must call onError before onClientError" }
                }
                onClientError {
                    assert(errorBody()?.string()?.contains("Unauthorized") == true)
                    resume()
                }
                onServerError {
                    assert(true == false) { "onServerError shouldn't be call" }
                }
            }
        }
    }

    @Test fun onServerError() {
        server.enqueue {
            setResponseCode(500)
            setBody("""{"message": "Internal Server Error","code": 500}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onError {
                    assert(code() == 500) { "Must call onError before onClientError" }
                }
                onClientError {
                    assert(true == false) { "onClientError shouldn't be call" }
                }
                onServerError {
                    assert(errorBody()?.string()?.contains("Internal") == true)
                    resume()
                }
            }
        }
    }

    @Test
    fun postSuccess() {
        server.enqueue {
            setResponseCode(201)
            setBody("""{"message": "created", "code": 201}""")
        }

        waitFor {
            service.sendMessage(Messaging(200, "Hello World")).enqueue {
                onSuccess {
                    assert(body()?.code == 201)
                    assert(body()?.message == "created")
                }
                finally {
                    resume()
                }
            }
        }
    }
}
