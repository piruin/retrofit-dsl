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

open class DslCallback<T> : Callback<T> {

    private var onSuccess: ((Response<T>) -> Unit)? = null
    private var onError: ((Response<T>) -> Unit)? = null
    private var onFailure: ((t: Throwable) -> Unit)? = null

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure?.invoke(t)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess?.invoke(response)
        } else {
            onError?.invoke(response)
        }
    }

    fun onSuccess(listener: Response<T>.() -> Unit) {
        onSuccess = listener
    }

    fun onError(listener: Response<T>.() -> Unit) {
        onError = listener
    }

    fun onFailure(listener: (t: Throwable) -> Unit) {
        onFailure = listener
    }
}

inline fun <T> Call<T>.enqueue(callback: DslCallback<T> = DslCallback(), block: DslCallback<T>.() -> Unit) {
    enqueue(callback.apply(block))
}
