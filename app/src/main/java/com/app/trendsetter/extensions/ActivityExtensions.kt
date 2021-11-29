package com.app.trendsetter.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt


fun Activity.setFullScreenFlags(isFullScreen: Boolean) {
    if (isFullScreen) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Activity.setStatusBarColor(@ColorInt color: Int, isLight: Boolean) {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = window.decorView.systemUiVisibility
            flags = if (isLight) flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            else flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
        }
    } else {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}

fun Activity.setStatusBarColorGrey() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 //       setStatusBarColor(ContextCompat.getColor(this, R.color.off_white), false)
    } else {
 //       setStatusBarColor(ContextCompat.getColor(this, R.color.warm_grey_two), true)
    }
}

fun Activity.setStatusBarColorTransparent(isLight: Boolean = isDarkModeEnabled()) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
  //      setStatusBarColor(Color.TRANSPARENT, isLight)
    } else {
 //       setStatusBarColor(ContextCompat.getColor(this, R.color.warm_grey_two), isLight)
    }
}

fun Activity.isDarkModeEnabled(): Boolean {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true // Dark mode is active
        Configuration.UI_MODE_NIGHT_NO -> false // Dark mode is not active
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}

fun Activity.hideKeyPad(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}
