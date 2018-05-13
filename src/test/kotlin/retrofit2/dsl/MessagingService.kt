package retrofit2.dsl

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MessagingService {
    @GET("message") fun getMessage(): Call<Messaging>

    @POST("message") fun sendMessage(@Body msg: Messaging): Call<Messaging>
}

data class Messaging(val code: Int?, val message: String)
