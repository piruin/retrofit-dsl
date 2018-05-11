# Retrofit-DSL
[![Build Status](https://travis-ci.org/piruin/retrofit-dsl.svg?branch=master)](https://travis-ci.org/piruin/retrofit-dsl)

Retrofit itself is great but it not design for Kotlin. This small plug-in library will make Retrofit look great on your `.kt`.

With Retrofit-DSL our code will look like below

```kotlin
 var service = retrofitFor<MessagingService> {
        baseUrl(server.url(""))
        addConverterFactory(GsonConverterFactory.create())
    }
 service.getMessage().enqueue {
     onSuccess { //this: Response<T>
        println(body())
     }
     onError { //this: Response<T>
        println(errorBody())
     }
     onFailure { //it: Throwable
        it.printStackTrace()
     }
 }
```

Isn't it good?

## Download

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.piruin:retrofit2-dsl:{latest-version}"
}
```

## License

    Copyright (c) 2018 Piruin Panichphol
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
