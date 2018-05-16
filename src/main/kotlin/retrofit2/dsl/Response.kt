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

import retrofit2.Response

val <T> Response<T>.isOK
    get() = code() == 200

val <T> Response<T>.isCreated
    get() = code() == 201

val <T> Response<T>.isAccepted
    get() = code() == 202

val <T> Response<T>.isMovedPermanently
    get() = code() == 301

val <T> Response<T>.isNotModified
    get() = code() == 304

val <T> Response<T>.isBadRequest
    get() = code() == 400

val <T> Response<T>.isUnauthorized
    get() = code() == 401

val <T> Response<T>.isForbidden
    get() = code() == 403

val <T> Response<T>.isNotFound
    get() = code() == 404

val <T> Response<T>.isMethodNotAllowed
    get() = code() == 405

val <T> Response<T>.isNotAcceptable
    get() = code() == 406

val <T> Response<T>.isInternalServerError
    get() = code() == 500

val <T> Response<T>.isNotImplemented
    get() = code() == 501

val <T> Response<T>.isBadGateway
    get() = code() == 502

val <T> Response<T>.isServiceUnavailable
    get() = code() == 503
