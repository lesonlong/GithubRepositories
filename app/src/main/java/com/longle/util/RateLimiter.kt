package com.longle.util

import android.os.SystemClock
import androidx.collection.ArrayMap
import com.longle.testing.OpenForTesting

import java.util.concurrent.TimeUnit

/**
 * Utility class that decides whether we should fetch some data or not.
 */
@OpenForTesting
class RateLimiter(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = ArrayMap<String, Long>()
    private val timeout = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: String): Boolean {
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            return true
        }
        return false
    }

    private fun now() = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: String) {
        timestamps.remove(key)
    }
}
