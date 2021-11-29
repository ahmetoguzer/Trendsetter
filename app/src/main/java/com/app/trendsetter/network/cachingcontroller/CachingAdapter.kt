package com.app.trendsetter.network.cachingcontroller

interface CachingAdapter<T> {

    fun retrieveData(callback: (T?) -> Unit)
    /**
     * Apply some checks on the retrieved data if exist otherwise it retrieve the data through a network call
     * return if true the retrieveData returns the data otherwise retrieve the data through a network call
     */
    fun retrieveOnCondition(data: T): Boolean = true

    /**
     * @param data the data to be saved
     * @param onSuccess called when data saved successfully
     */
    fun saveData(data: T, onSuccess: () -> Unit)
}
