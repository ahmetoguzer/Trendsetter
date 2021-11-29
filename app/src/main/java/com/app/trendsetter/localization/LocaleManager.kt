
package com.app.trendsetter.localization

import java.util.*

object LocaleManager {
    private var locale: Locale = Locale.getDefault()

    fun setLocale(locale: Locale) {
        LocaleManager.locale = locale
    }

    fun getLocale(): Locale = locale
}
