package com.app.trendsetter.logger

/**
 * Interface definition for a callbacks to be invoked when a message was logged.
 */

interface LogListener {

    /**
     * Called when a message has been logged together with a `Throwable` object.
     * @param level The logging level.
     * @param tag Used to identify the source of a log message. It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message to be logged.
     * @param throwable An exception to log.
     */
    fun onLog(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
}
