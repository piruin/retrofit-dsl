package retrofit2.dsl.paging

import retrofit2.Response

interface PagingAdapter {
    fun <T> parse(response: Response<T>)

    val next: Int
    val last: Int
    val perPage: Int
}
