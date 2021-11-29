package com.app.trendsetter.extensions

import android.view.View
import com.app.trendsetter.logger.Log


fun View.setIdFromString(key: String) {
    val idRes = resources.getIdentifier(key, "id", context.packageName)
    if (idRes != 0) {
        id = idRes
    } else {
        Log.e(View::class.java.simpleName, "Add this id: $key on the resource ids file.")
    }
}
