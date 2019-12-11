package com.longle.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.longle.data.Result.Status.ERROR
import com.longle.data.Result.Status.SUCCESS
import kotlinx.coroutines.Dispatchers

/**
 * The database serves as the single source of truth.
 * Therefore UI can receive data updates from database only.
 * Function notify UI about:
 * [Result.Status.SUCCESS] - with data from database
 * [Result.Status.ERROR] - if error has occurred from any source
 * [Result.Status.LOADING]
 */
fun <T, A> resultLiveData(
    databaseQuery: () -> LiveData<T>,
    shouldFetch: () -> Boolean,
    networkCall: suspend () -> Result<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Result<T>> =
    liveData(Dispatchers.IO) {
        emit(Result.loading())
        val source = databaseQuery.invoke().map { Result.success(it) }
        emitSource(source)

        if (shouldFetch.invoke()) {
            val responseStatus = networkCall.invoke()
            if (responseStatus.status == SUCCESS) {
                responseStatus.data?.let { data -> saveCallResult(data) }
            } else if (responseStatus.status == ERROR) {
                emit(Result.error(responseStatus.message))
                emitSource(source)
            }
        }
    }