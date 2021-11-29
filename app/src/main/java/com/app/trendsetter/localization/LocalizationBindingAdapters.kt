package com.app.trendsetter.localization

import android.widget.TextView
import androidx.databinding.BindingAdapter

object LocalizationBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:textByKey", "app:textByKeyFormatArgs", requireAll = false)
    fun setTextViewTextFromString(textView: TextView, key: String?, format: Array<String>? = null) {
        textView.text = VFGContentManager.getValue(key, format ?: arrayOf())
    }

    @JvmStatic
    @BindingAdapter("app:textByKeyWithContentUpdateListener", "app:textByKeyFormatArgs", requireAll = false)
    fun setTextViewTextFromStringWithContentUpdateListener(
            textView: TextView,
            key: String?,
            format: Array<String>? = null
    ) {
        setTextViewTextFromString(textView, key, format)
        key?.also {
            VFGContentManager.addContentMapUpdateListener(it) {
                setTextViewTextFromString(textView, key, format)
            }
        }
    }
}
