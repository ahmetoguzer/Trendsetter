package com.app.trendsetter.app

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.webkit.WebView
import androidx.appcompat.app.AppCompatDelegate
import com.app.trendsetter.app.initializer.AppInitializers
import com.app.trendsetter.di.DaggerAppInitializerComponent
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GlobalApplication : Application() {

    @Inject
    lateinit var initializers: AppInitializers

    override fun onCreate() {
        super.onCreate()
        instance = this
        DaggerAppInitializerComponent.builder().build()
        initApp()
        initializers.init(this)
    }

    private fun initApp() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        try {
            Class.forName("android.os.AsyncTask")
        } catch (ignore: Throwable) {
        }
        PACKAGE_NAME = applicationContext.packageName
        setDataDirectorySuffix()
    }

    private fun setDataDirectorySuffix() {
        // https://developer.android.com/about/versions/pie/android-9.0-changes-28#web-data-dirs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (PACKAGE_NAME != getProcessName()) {
                getProcessName(applicationContext)?.let {
                    WebView.setDataDirectorySuffix(it)
                }
            }
        }
    }

    private fun getProcessName(context: Context): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.runningAppProcesses.forEach { processInfo ->
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName
            }
        }
        return null
    }

    companion object {
        lateinit var instance: GlobalApplication

        var PACKAGE_NAME: String? = null
    }
}