/*
 * Copyright (c) 2018 Piruin Panichphol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package retrofit2.dsl

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.dsl.paging.Page

open class DslCallback<T> : Callback<T> {

    lateinit var call: Call<T>
    var pagingAdapter = RetrofitDslConfig.pagingAdapter
    var page: Page? = null
    var always: (() -> Unit)? = null
    var onSuccess: ((Response<T>) -> Unit)? = null
    var onRedirect: ((Response<T>) -> Unit)? = null
    var onError: ((Response<T>) -> Unit)? = null
    var onClientError: ((Response<T>) -> Unit)? = null
    var onServerError: ((Response<T>) -> Unit)? = null
    var onFailure: ((t: Throwable) -> Unit)? = null
    var finally: (() -> Unit)? = null

    final override fun onFailure(call: Call<T>, t: Throwable) {
        this.call = call
        always?.invoke()
        onFailure?.invoke(t)
        finally?.invoke()
    }

    final override fun onResponse(call: Call<T>, response: Response<T>) {
        this.call = call
        always?.invoke()
        if (response.isSuccessful) {
            page = pagingAdapter?.parse(response)
            onSuccess?.invoke(response)
        } else {
            onNotSuccess(response)
        }
        finally?.invoke()
    }

    fun onNotSuccess(response: Response<T>) {
        if (response.code() in 400..599)
            onError?.invoke(response)
        when (response.code()) {
            in 300..399 -> onRedirect?.invoke(response)
            in 400..499 -> onClientError?.invoke(response)
            in 500..599 -> onServerError?.invoke(response)
        }
    }

    fun always(listener: () -> Unit) {
        always = listener
    }

    fun onSuccess(listener: Response<T>.() -> Unit) {
        onSuccess = listener
    }

    fun onRedirect(listener: Response<T>.() -> Unit) {
        onRedirect = listener
    }

    fun onError(listener: Response<T>.() -> Unit) {
        onError = listener
    }

    fun onClientError(listener: Response<T>.() -> Unit) {
        onClientError = listener
    }

    fun onServerError(listener: Response<T>.() -> Unit) {
        onServerError = listener
    }

    fun onFailure(listener: (t: Throwable) -> Unit) {
        onFailure = listener
    }

    fun finally(listener: () -> Unit) {
        finally = listener
    }

    /**
     * Get `errorBody()` of `Response<T>` as desire type by `responseBodyConverter` of `retrofit`
     *
     * @param K Type of `errorBody()` to be parsed
     * @param retrofit Source of responseBodyConverter. default is RestrofitDslConfig.retrofit
     * @return errorBody() as desire type. nullable
     *
     * @throws IllegalArgumentException When both RestrofitDslConfig.retrofit and pass argument is `null`
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    inline fun <reified K> Response<T>.errorBody(retrofit: Retrofit? = RetrofitDslConfig.retrofit): K? {
        require(retrofit != null) { "RetrofitDslConfig.retrofit should be set or passed as method argument" }
        if (errorBody() != null) {
            return retrofit!!.converterFor<K>().convert(errorBody()!!)
        }
        return null
    }
}

inline fun <T> Call<T>.enqueue(callback: DslCallback<T> = DslCallback(), block: DslCallback<T>.() -> Unit) {
    enqueue(callback.apply(block))
}
