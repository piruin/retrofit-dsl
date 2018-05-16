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
            setBody("""{"code":300, "message": "Redirect"}""")
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
                    assert(errorBody<Messaging>() == Messaging(300, "Redirect"))
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
                    assert(errorBody<Messaging>() == Messaging(400, "Bad Request"))
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
                    assert(errorBody<Messaging>()?.message == "Unauthorized")
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
