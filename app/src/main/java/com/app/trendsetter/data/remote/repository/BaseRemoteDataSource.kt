package com.app.trendsetter.data.remote.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.app.trendsetter.BuildConfig
import com.app.trendsetter.data.remote.Resource
import com.app.trendsetter.network.networkservice.Request
import com.app.trendsetter.network.networkservice.apiCall
import com.app.trendsetter.network.networkservice.cacheableApiCall
import com.readystatesoftware.chuck.ChuckInterceptor
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class BaseRemoteDataSource constructor(
    private val context: Context? = null
) {

    @SuppressWarnings("TooGenericExceptionCaught")
    @WorkerThread
    private suspend fun <T> fetchResult(call: suspend () -> T): Resource<T> =
        try {
            val response = call()
            Resource.success(response)
        } catch (e: Throwable) {
            Resource.error(e)
        }

    private suspend fun <T> requestSuspendWrapper(
        block: (successCallback: (T) -> Unit, errorCallback: (Throwable) -> Unit) -> Unit
    ) = suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        val successCallback = { it: T ->
            cont.resume(it)
        }

        val errorCallback = { it: Throwable ->
            cont.resumeWithException(it)
        }

        block(successCallback, errorCallback)
    }

    protected suspend fun <T> makeRequestWith(
        builder: Request.Builder,
        isCacheable: Boolean = false
    ) = fetchResult {
        requestSuspendWrapper { successCallback: (T) -> Unit, errorCallback: (Throwable) -> Unit ->
            if (BuildConfig.DEBUG && context != null) {
                builder.setChuckInterceptor(ChuckInterceptor(context))
            }
            if (isCacheable) {
                cacheableApiCall(
                    builder,
                    responseCallback = successCallback,
                    errorCallback = errorCallback
                )
            } else {
                apiCall(
                    builder,
                    responseCallback = successCallback,
                    errorCallback = errorCallback
                )
            }
        }
    }
}
