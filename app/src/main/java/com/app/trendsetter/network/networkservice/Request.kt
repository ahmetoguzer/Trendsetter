package com.app.trendsetter.network.networkservice

import com.app.trendsetter.network.cachingcontroller.CachingAdapter
import com.app.trendsetter.network.config.ParserFactory
import com.app.trendsetter.network.config.RetrofitConfig
import okhttp3.Interceptor

@SuppressWarnings("LongParameterList")
class Request<T> private constructor(
    val retrofitConfig: RetrofitConfig.Builder?,
    val url: String,
    val body: Any?,
    val headers: Map<String, String>?,
    val queryParams: Map<String, String>?,
    val fieldParams: Map<String, String>?,
    var requestType: RequestType,
    var cachingAdapter: CachingAdapter<T>? = null,
    val response: Class<T>,
    var parserFactory: ParserFactory<T>? = null,
    val logToggle: Boolean?,
    val chuckInterceptor: Interceptor? = null
) {

    @SuppressWarnings("TooManyFunctions")
    data class Builder(
        internal var retrofitConfig: RetrofitConfig.Builder? = null,
        private var requestType: RequestType? = null,
        private var url: String? = null,
        private var body: Any? = null,
        private var response: Class<*>? = null,
        private var cachingAdapter: CachingAdapter<*>? = null,
        private var parserFactory: ParserFactory<*>? = null,
        private var logToggle: Boolean? = null,
        private var chuckInterceptor: Interceptor? = null
    ) {
        private val headers: MutableMap<String, String>? by lazy { mutableMapOf<String, String>() }
        private val queryParams: MutableMap<String, String>? by lazy { mutableMapOf<String, String>() }
        private val fieldParams: MutableMap<String, String>? by lazy { mutableMapOf<String, String>() }

        /**
         * @param url the required full url
         * @param requestType request Http method type, the default is RequestType.GET
         */
        fun url(url: String, requestType: RequestType? = null): Builder {
            this.requestType = requestType
            this.url = url
            return this
        }

        internal fun setRetrofitConfig(retrofitConfig: RetrofitConfig.Builder?): Builder {
            this.retrofitConfig = retrofitConfig
            return this
        }

        fun body(body: Any?): Builder {
            this.body = body
            return this
        }

        fun setChuckInterceptor(chuckInterceptor: Interceptor?): Builder {
            this.chuckInterceptor = chuckInterceptor
            return this
        }

        fun addField(key: String, value: String): Builder {
            fieldParams?.put(key, value)
            return this
        }

        fun addFields(params: Map<String, String>): Builder {
            fieldParams?.putAll(params)
            return this
        }

        fun removeField(key: String): Builder {
            fieldParams?.remove(key)
            return this
        }

        fun addHeader(key: String, value: String): Builder {
            headers?.put(key, value)
            return this
        }

        fun addHeaders(params: Map<String, String>): Builder {
            headers?.putAll(params)
            return this
        }

        fun removeHeader(key: String): Builder {
            headers?.remove(key)
            return this
        }

        fun addQueryParam(key: String, value: String): Builder {
            queryParams?.put(key, value)
            return this
        }

        fun addQueryParams(params: Map<String, String>): Builder {
            queryParams?.putAll(params)
            return this
        }

        fun removeQueryParam(key: String): Builder {
            queryParams?.remove(key)
            return this
        }

        fun <T> response(clazz: Class<T>): Builder {
            response = clazz
            return this
        }

        fun <T> parserFactory(parserFactory: ParserFactory<T>): Builder {
            this.parserFactory = parserFactory
            return this
        }

        fun <T> cachingAdapter(cachingAdapter: CachingAdapter<T>): Builder {
            this.cachingAdapter = cachingAdapter
            return this
        }

        /**
         * Enable logging the body and header of the request.
         * Logger is set by default to true in the debug mode and false in release however you can enable it in for testing only.
         */
        fun enableLogger(logToggle: Boolean): Builder {
            this.logToggle = logToggle
            return this
        }

        fun <T> build(): Request<T> {
            checkNotNull(response) { "unknown response type, set the response type using the response() method" }
            return Request(
                retrofitConfig,
                checkNotNull(url) { "Url cannot be null" },
                body,
                headers,
                queryParams,
                fieldParams,
                requestType ?: RequestType.GET,
                if (cachingAdapter == null) null else cachingAdapter as CachingAdapter<T>,
                response as Class<T>,
                if (parserFactory == null) null else parserFactory as ParserFactory<T>,
                logToggle,
                chuckInterceptor
            )
        }
    }
}
