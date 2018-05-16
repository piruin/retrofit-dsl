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

    val retrofit = retrofit {
        baseUrl(server.url(""))
        addConverterFactory(GsonConverterFactory.create())
    }

    @Before fun setUp() {
        service = retrofit.create()
        DslCallback.defaultRetrofit = retrofit
    }

    @Test fun onSuccess() {
        server.enqueue {
            setResponseCode(200)
            setBody("""{"code": 200, "message": "Hello"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                var isAlwaysCall = false
                always {
                    isAlwaysCall = true
                }
                onSuccess {
                    assert(isOK)
                    assert(body() == Messaging(200, "Hello"))
                }
                finally {
                    assert(isAlwaysCall) { "always block should be called" }
                    resume()
                }
            }
        }
    }

    @Test fun onRedirect() {
        server.enqueue {
            setResponseCode(301)
            setBody("""{"code":301, "message": "Moved Permanently"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    assert(true == false) { "onSuccess shouldn't be call" }
                }
                onError {
                    assert(true == false) { "onError shouldn't be call" }
                }
                onRedirect {
                    assert(isMovedPermanently)
                    assert(errorBody<Messaging>() == Messaging(301, "Moved Permanently"))
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
                    assert(isAlwaysCall) { "always block should also be called when failure" }
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
                    assert(isBadRequest)
                    assert(errorBody<Messaging>() == Messaging(400, "Bad Request"))
                    resume()
                }
            }
        }
    }

    @Test fun onClientError() {
        server.enqueue {
            setResponseCode(401)
            setBody("""{"message": "Unauthorized", "code": 401 }""")
        }

        waitFor {
            service.getMessage().enqueue {
                onError {
                    assert(code() == 401) { "Must call onError before onClientError" }
                }
                onClientError {
                    assert(isUnauthorized)
                    assert(errorBody<Messaging>() == Messaging(401, "Unauthorized"))
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
            setBody("""{"message": "Internal Server Error", "code": 500 }""")
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
                    assert(isInternalServerError)
                    assert(errorBody<Messaging>()?.message == "Internal Server Error")
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
                    assert(isCreated)
                    assert(body() == Messaging(201, "created"))
                }
                finally {
                    resume()
                }
            }
        }
    }
}
