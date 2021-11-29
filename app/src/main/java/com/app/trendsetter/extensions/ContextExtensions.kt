package com.app.trendsetter.extensions

import android.content.Context
import android.content.res.Resources

fun Context.getResourceName(resId: Int?): String? {
    return resId?.let {
        try {
             resources.getResourceEntryName(resId)
        } catch (_: Resources.NotFoundException) {
            null
        }
    }
}
