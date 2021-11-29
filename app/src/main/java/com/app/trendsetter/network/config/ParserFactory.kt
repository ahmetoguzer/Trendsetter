package com.app.trendsetter.network.config

import com.google.gson.Gson

interface ParserFactory<T> {
    fun parse(input: String, type: Class<T>): T
}

internal class DefaultParser<T> : ParserFactory<T> {
    override fun parse(input: String, type: Class<T>): T = Gson().fromJson<T>(input, type)
}
