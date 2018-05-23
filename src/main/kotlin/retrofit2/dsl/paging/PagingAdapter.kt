package retrofit2.dsl.paging

import retrofit2.Response

interface PagingAdapter {
    fun <T> parse(response: Response<T>): Page?
}

data class Page(val next: Int, val last: Int, val perPage: Int)
