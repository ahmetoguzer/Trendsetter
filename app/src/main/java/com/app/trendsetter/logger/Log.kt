package com.app.trendsetter.logger

import android.util.Log
import com.app.trendsetter.logger.LogLevel.*
import java.util.*

/**
 * The `Log` class provides logging services, handles filtering setup and log listeners registration.
 */

object Log {

    private var logToLogcat = true

    private var globalLogLevel = VERBOSE

    private var allowedLogLevels = LogLevel.values()

    private val listeners = ArrayList<LogListener>()

    private fun notifyListeners(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        for (listener in listeners) {
            listener.onLog(level, tag, message, throwable)
        }
    }

    private fun doLog(level: LogLevel, tag: String, message: String, throwable: Throwable? = null) {
        if (isLoggingAllowed(level, tag)) {
            if (logToLogcat) {
                when (level) {
                    VERBOSE -> Log.v(tag, message, throwable)
                    DEBUG -> Log.d(tag, message, throwable)
                    INFO -> Log.i(tag, message, throwable)
                    WARN -> Log.w(tag, message, throwable)
                    ERROR -> Log.e(tag, message, throwable)
                }
            }

            notifyListeners(level, tag, message, throwable)
        }
    }

    private fun isLoggingAllowed(level: LogLevel, tag: String): Boolean {
        return if (globalLogLevel.ordinal <= level.ordinal && allowedLogLevels.contains(level)) {
            TagFilter.isLoggingAllowedForTag(tag)
        } else false
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        doLog(VERBOSE, tag, message, throwable)
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        doLog(DEBUG, tag, message, throwable)
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        doLog(INFO, tag, message, throwable)
    }

    /**
     * Send a WARN log message.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        doLog(WARN, tag, message, throwable)
    }

    /**
     * Send a ERROR log message and log the exception.
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param message The message you would like logged.
     * @param throwable An exception to log
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        doLog(ERROR, tag, message, throwable)
    }

    object LogConfig {

        /**
         * Enables showing logs for given tag
         *
         * @param tag The tag for which logs should be shown
         */
        @Synchronized
        fun enableLogsForTag(tag: String) {
            TagFilter.enableLogsForTag(tag)
        }

        /**
         * Disables showing logs for given tag
         *
         * @param tag The tag for which logs should not be shown
         */
        @Synchronized
        fun disableLogsForTag(tag: String) {
            TagFilter.disableLogsForTag(tag)
        }

        /**
         * Enables logs for all tags
         */
        @Synchronized
        fun enableAllTags() {
            TagFilter.enableAllTags()
        }

        /**
         * Disables logs for all tags
         */
        @Synchronized
        fun disableAllTags() {
            TagFilter.disableAllTags()
        }

        /**
         * States if logging to logcat should be enabled or not
         *
         * @param enabled if `true` then log to logcat, if `false` do not log to logcat.
         */
        @Synchronized
        fun logToLogcat(enabled: Boolean) {
            logToLogcat = enabled
        }

        /**
         * Registers listener to be notified about logs being logged
         *
         * @param listener The listener to be registered
         */
        @Synchronized
        fun registerListener(listener: LogListener) {
            listeners.add(listener)
        }

        /**
         * Unregisters listener to not be notified about logs being logged
         *
         * @param listener The listener to be unregistered
         */
        @Synchronized
        fun unregisterListener(listener: LogListener) {
            listeners.remove(listener)
        }

        /**
         * Sets minimal allowed level for logs. All logs below this level will be ignored.
         *
         * @param level The level to be set
         */
        @Synchronized
        fun setGlobalLevel(level: LogLevel) {
            globalLogLevel = level
        }

        /**
         * Sets level filter for logs. All logs with different levels will be ignored.
         *
         * @param allowedLevels Allowed levels for logs to be set
         */
        @Synchronized
        fun setGlobalFilter(allowedLevels: Array<LogLevel>) {
            allowedLogLevels = allowedLevels
        }

        /**
         * Unsets level filter for logs. Logs for all levels will be shown.
         */
        @Synchronized
        fun unsetGlobalFilter() {
            allowedLogLevels = LogLevel.values()
        }
    }

    /**
     * The `TagFilter` class provides internal helper methods for log filtering.
     */
    object TagFilter {

        /**
         * State of tag filter
         */
        private var tagFilterState = TagFilterState.ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN

        /**
         * List of tags used for filtering logs -->  containing knownTagsList
         */
        private val filteredTags = HashSet<String>()

        private enum class TagFilterState {
            /**
             * Allow all unknown tags, block known tags
             */
            ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN,
            /**
             * Block all unknown tags, allow known tags
             */
            BLOCK_UNKNOWN_TAGS_ALLOW_KNOWN
        }

        fun enableLogsForTag(tag: String) {
            if (tagFilterState == TagFilterState.ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN) {
                filteredTags.remove(tag)
            } else {
                filteredTags.add(tag)
            }
        }

        fun disableLogsForTag(tag: String) {
            if (tagFilterState == TagFilterState.ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN) {
                filteredTags.add(tag)
            } else {
                filteredTags.remove(tag)
            }
        }

        fun enableAllTags() {
            filteredTags.clear()
            tagFilterState = TagFilterState.ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN
        }

        fun disableAllTags() {
            filteredTags.clear()
            tagFilterState = TagFilterState.BLOCK_UNKNOWN_TAGS_ALLOW_KNOWN
        }

        fun isLoggingAllowedForTag(tag: String): Boolean {
            val tagFilterExists = filteredTags.contains(tag)
            return if (tagFilterState == TagFilterState.ALLOW_UNKNOWN_TAGS_BLOCK_KNOWN) {
                !tagFilterExists
            } else {
                tagFilterExists
            }
        }
    }
}
