package com.app.trendsetter.network.networkservice

import com.app.trendsetter.network.config.RetrofitConfig
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface NetworkService<Response, RestClient> {
    var retrofitConfig: RetrofitConfig

    fun execute(
            observable: Observable<Response>,
            responseCallback: (Response) -> Unit,
            errorCallback: (Throwable) -> Unit
    ): Disposable

    fun createRestClient(tClass: Class<RestClient>): RestClient
}
