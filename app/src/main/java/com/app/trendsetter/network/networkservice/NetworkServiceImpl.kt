package com.app.trendsetter.network.networkservice


import com.app.trendsetter.network.config.RetrofitConfig
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor

open class NetworkServiceImpl<Response, RestClient> : NetworkService<Response, RestClient> {
    override lateinit var retrofitConfig: RetrofitConfig

    fun init(url: String, enableLogger: Boolean?, chuckInterceptor: Interceptor?) {
        retrofitConfig = if(chuckInterceptor!=null){
            RetrofitConfig.Builder().url(url).enableLogger(enableLogger).addInterceptor(chuckInterceptor).build()
        }else{
            RetrofitConfig.Builder().url(url).enableLogger(enableLogger).build()
        }
    }

    override fun execute(
        observable: Observable<Response>,
        responseCallback: (Response) -> Unit,
        errorCallback: (Throwable) -> Unit
    ): Disposable = observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            responseCallback(it)
        }, {
            errorCallback(it)
        })

    override fun createRestClient(tClass: Class<RestClient>): RestClient {
        return retrofitConfig.createClient(tClass)
    }
}

fun <Response, RestClient> createCustomNetworkService(
    retrofitConfig: RetrofitConfig.Builder?,
    enableLogger: Boolean? = null,
    url: String,
    chuckInterceptor: Interceptor?,
    service: NetworkService<Response, RestClient>.() -> Unit
) =
    NetworkServiceImpl<Response, RestClient>().apply {
        if (retrofitConfig != null) {
            if(chuckInterceptor!=null){
                this.retrofitConfig = retrofitConfig.url(url).enableLogger(enableLogger).addInterceptor(chuckInterceptor).build()
            }else{
                this.retrofitConfig = retrofitConfig.url(url).enableLogger(enableLogger).build()
            }
        } else init(url, enableLogger, chuckInterceptor)
        service(this)
    }

fun <Response> createNetworkService(
    url: String,
    enableLogger: Boolean? = null,
    chuckInterceptor: Interceptor?,
    service: NetworkService<Response, DefaultRestClient>.() -> Unit
) = createCustomNetworkService(null, enableLogger, url, chuckInterceptor, service)

fun <Response> createNetworkService(
    retrofitConfig: RetrofitConfig.Builder?,
    enableLogger: Boolean? = null,
    url: String,
    chuckInterceptor: Interceptor?,
    service: NetworkService<Response, DefaultRestClient>.() -> Unit
) = createCustomNetworkService(retrofitConfig, enableLogger, url, chuckInterceptor, service)
