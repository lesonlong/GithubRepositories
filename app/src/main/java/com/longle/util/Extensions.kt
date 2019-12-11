package com.longle.util

import android.view.View
import androidx.lifecycle.LiveData
import com.longle.data.Result
import com.longle.data.repository.NetworkState
import com.longle.view.PagingRequestHelper
import retrofit2.Response
import timber.log.Timber

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

suspend fun <T> (suspend () -> Response<T>).toResult(): Result<T> {
    try {
        val response = invoke()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return Result.success(body)
        }
        return error(" ${response.code()} ${response.message()}")
    } catch (e: Exception) {
        return error(e.message ?: e.toString())
    }
}

private fun <T> error(message: String): Result<T> {
    Timber.e(message)
    return Result.error("Network call has failed for a following reason: $message")
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = androidx.lifecycle.MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(
                NetworkState.error(getErrorMessage(report))
            )
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}
