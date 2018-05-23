package retrofit2.dsl.paging

import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.dsl.MessagingService
import retrofit2.dsl.RetrofitDslConfig
import retrofit2.dsl.create
import retrofit2.dsl.enqueue
import retrofit2.dsl.retrofit
import retrofit2.dsl.waitFor

class GithubPagingAdapterTest {

    @get:Rule val server = MockWebServer()

    lateinit var service: MessagingService

    val retrofit = retrofit {
        baseUrl(server.url(""))
        addConverterFactory(GsonConverterFactory.create())
    }

    @Before fun setUp() {
        service = retrofit.create()
        RetrofitDslConfig.retrofit = retrofit
        RetrofitDslConfig.pagingAdapter = GithubPagingAdapter()
    }

    @Test
    fun test() {
        server.enqueue {
            setResponseCode(200)
            setHeader("Link", """
                |<https://api.github.com/user/repos?page=3&per_page=100>; rel="next",
                |<https://api.github.com/user/repos?page=50&per_page=100>; rel="last"
                """.trimMargin().removeNewLine())
            setBody("""{"code": 200, "message": "Hello"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    assert(page?.next == 3)
                    assert(page?.last == 50)
                    assert(page?.perPage == 100)
                    resume()
                }
            }
        }
    }

    @Test
    fun testRearrageParam() {
        server.enqueue {
            setResponseCode(200)
            setHeader("Link", """
                |<https://api.github.com/user/repos?per_page=500&page=2>; rel="next",
                |<https://api.github.com/user/repos?per_page=500&page=9>; rel="last"
                """.trimMargin().removeNewLine())
            setBody("""{"code": 200, "message": "Hello"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    assert(page?.next == 2)
                    assert(page?.last == 9)
                    assert(page?.perPage == 500)
                    resume()
                }
            }
        }
    }

    @Test
    fun explicitHeader() {
        server.enqueue {
            setResponseCode(200)
            setHeader(GithubPagingAdapter.HEADER_NEXT, """https://api.github.com/user/repos?per_page=300&page=10""")
            setHeader(GithubPagingAdapter.HEADER_LAST, """https://api.github.com/user/repos?per_page=300&page=15""")
            setBody("""{"code": 200, "message": "Hello"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    assert(page?.next == 10)
                    assert(page?.last == 15)
                    assert(page?.perPage == 300)
                    resume()
                }
            }
        }
    }

    @Test
    fun noPagingHeader() {
        server.enqueue {
            setResponseCode(200)
            setBody("""{"code": 200, "message": "Hello"}""")
        }

        waitFor {
            service.getMessage().enqueue {
                onSuccess {
                    resume()
                }
            }
        }
    }

    fun String.removeNewLine() = this.replace("\n", "")
}
