package com.app.trendsetter.network.networkservice

import com.app.trendsetter.network.config.DefaultParser
import com.app.trendsetter.network.config.ParserFactory
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import okhttp3.ResponseBody

internal class ApiCallCreator {
    internal fun <T> call(
        request: Request<T>,
        responseCallback: (T) -> Unit,
        errorCallback: (Throwable) -> Unit
    ) =
            createNetworkService<T>(request.retrofitConfig, request.logToggle, request.url,request.chuckInterceptor) {
                val restClient = createRestClient(DefaultRestClient::class.java)
                execute(toResponseType(request.parserFactory, when (request.requestType) {
                    RequestType.GET ->
                        restClient.getApiCall(request.url, request.headers, request.queryParams)
                    RequestType.PUT ->
                        restClient.putApiCall(request.url, request.headers, request.queryParams, request.body)
                    RequestType.DELETE ->
                        restClient.deleteApiCall(request.url, request.headers, request.queryParams)
                    RequestType.POST_FORM_URL ->
                        restClient.postFormUrlApiCall(request.url, request.headers, request.queryParams,
                                request.fieldParams)
                    RequestType.POST ->
                        restClient.postApiCall(request.url, request.headers, request.queryParams,
                                request.body)
                }, request.response), responseCallback, errorCallback)
            }

    private fun <T> toResponseType(
        parserFactory: ParserFactory<T>?,
        apiCall: Observable<ResponseBody>,
        clazz: Class<T>
    ): Observable<T> {
        return apiCall.flatMap { responseBody ->
            Observable.create(ObservableOnSubscribe<T> {
                it.onNext((parserFactory ?: DefaultParser()).parse(responseBody.string(), clazz))
            })
        }
    }
}

fun <Response> apiCall(
    builder: Request.Builder,
    responseCallback: (Response) -> Unit,
    errorCallback: (Throwable) -> Unit
) {
    ApiCallCreator().call(builder.build(), responseCallback, errorCallback)
}

/**
 * requires cachingAdapter to retrieve and save the data otherwise will work as apiCall method
 */
fun <Response> cacheableApiCall(
    builder: Request.Builder,
    responseCallback: (Response) -> Unit,
    errorCallback: (Throwable) -> Unit
) {
    val request = builder.build<Response>()
    if (request.cachingAdapter != null) {
        request.cachingAdapter!!.retrieveData { data ->
            if (data != null && request.cachingAdapter!!.retrieveOnCondition(data)) {
                responseCallback(data)
            } else {
                ApiCallCreator().call(request, {
                    request.cachingAdapter!!.saveData(it) {
                        responseCallback(it)
                    }
                }, errorCallback)
            }
        }
    } else {
        ApiCallCreator().call(request, responseCallback, errorCallback)
    }
}
