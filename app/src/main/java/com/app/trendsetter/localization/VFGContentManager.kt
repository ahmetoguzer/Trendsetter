package com.app.trendsetter.localization

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes
import com.app.trendsetter.extensions.getResourceName
import com.app.trendsetter.logger.Log
import java.util.*

interface ContentInterceptor :
        (String?, Array<String>?, Context, (String?, Array<String>?) -> String) -> String {
    override fun invoke(
            key: String?,
            formatArgs: Array<String>?,
            context: Context,
            contentMap: (String?, Array<String>?) -> String
    ): String
}

@SuppressLint("StaticFieldLeak")
object VFGContentManager {
    private val contentMap = hashMapOf<String, String>()
    private var activity: Activity? = null
    private val stringContentFetcher = StringContentFetcher()
    private var interceptor: ContentInterceptor? = null
    private var contentMapUpdateListenersMap: MutableMap<String, Function0<Unit>> = mutableMapOf()

    fun updateContent(map: Map<String, String>) {
        contentMap.clear()
        contentMap.putAll(map)
        contentMapUpdateListenersMap.values.forEach { it.invoke() }
    }

    fun addContentMapUpdateListener(key: String, onContentMapUpdateListener: () -> Unit) {
        contentMapUpdateListenersMap[key] = onContentMapUpdateListener
    }

    fun removeContentMapUpdateListener(key: String) {
        contentMapUpdateListenersMap.remove(key)
    }

    fun cleanAllContentMapUpdateListeners() {
        contentMapUpdateListenersMap.clear()
    }

    fun setInterceptor(interceptor: ContentInterceptor?) {
        VFGContentManager.interceptor = interceptor
    }

    fun init(app: Activity) {
        activity = app
    }

    fun getValue(key: String?, format: Array<String>? = null) = getValueActual(key, format)

    fun getValue(@StringRes stringId: Int?, format: Array<String>? = null): String {
        val key = activity?.getResourceName(stringId)
        return getValueActual(key, format)
    }

    private fun getValueActual(key: String?, format: Array<String>? = null): String {
        activity?.also { app ->
            interceptor?.also { interceptor ->
                return interceptor.invoke(
                    key,
                    format,
                    app
                ) { interceptorKey, interceptorFormatArgs ->
                    getValueFromContent(interceptorKey, interceptorFormatArgs)
                }
            }
        }
        return getValueFromContent(key, format)
    }

    private fun getValueFromContent(key: String?, format: Array<String>? = null): String {
        val value = stringContentFetcher(
            activity,
                contentMap,
                key
            )
        return value.safeFormat(format)
    }
}

@SuppressWarnings("SpreadOperator")
private fun String.safeFormat(args: Array<String>?): String {
    if (!args.isNullOrEmpty()) {
        try {
            return format(*args)
        } catch (e: IllegalFormatException) {
            Log.e("Formatting error", e.message.toString(), e)
        }
    }
    return this
}
