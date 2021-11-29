package com.app.trendsetter.extensions

import android.content.res.Configuration
import android.content.res.Resources

fun Resources.isDarkModeOn(): Boolean {
    val currentNightMode = configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}
