package com.app.trendsetter.localization

import android.content.Context

/**
 * A function to fetch a String from a given dictionary with its key. If the key doesn't exist, the value
 * is obtained from String resources with a matching key. If the key doesn't exist in resources,
 * then the key itself is returned. If a null key is provided, an empty String is returned instead.
 *
 * @param context The context
 * @param stringsMap The dictionary to search in.
 * @param key The key for the required String.
 */
class StringContentFetcher : (Context?, Map<String, String>, String?) -> String {
    override fun invoke(context: Context?, stringsMap: Map<String, String>, key: String?): String {
        val result = when {
            key == null -> ""
            stringsMap.containsKey(key) -> stringsMap[key]
            else -> context?.resources
                    ?.getIdentifier(key, "string", context.packageName)
                    .takeUnless { it == 0 }
                    ?.let { context!!.getString(it) }
        } ?: key
        return result ?: ""
    }
}
