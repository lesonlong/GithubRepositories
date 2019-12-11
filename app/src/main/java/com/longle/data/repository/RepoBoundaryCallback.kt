package com.longle.data.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.longle.data.Result.Status.ERROR
import com.longle.data.Result.Status.SUCCESS
import com.longle.data.api.RepoService
import com.longle.data.model.Repo
import com.longle.view.PagingRequestHelper
import com.longle.util.createStatusLiveData
import com.longle.util.toResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class RepoBoundaryCallback(
    private val service: RepoService,
    private val handleResponse: (List<Repo>) -> Unit,
    private val networkPageSize: Int,
    private val coroutineScope: CoroutineScope,
    private val coroutineExceptionHandler: CoroutineExceptionHandler
) : PagedList.BoundaryCallback<Repo>() {

    val helper = PagingRequestHelper()
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            coroutineScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                val response = fetchRepos(1, networkPageSize)
                if (response.status == SUCCESS) {
                    response.data?.let { data -> insertItemsIntoDb(data, it) }

                } else if (response.status == ERROR) {
                    it.recordFailure(Exception(response.message))
                }
            }
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            coroutineScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                val response = fetchRepos(itemAtEnd.pageIndex + 1, networkPageSize)
                if (response.status == SUCCESS) {
                    response.data?.let { data -> insertItemsIntoDb(data, it) }
                } else if (response.status == ERROR) {
                    it.recordFailure(Exception(response.message))
                }
            }
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
        repos: List<Repo>,
        it: PagingRequestHelper.Request.Callback
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            handleResponse(repos)
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Repo) {
        // ignored, since we only ever append to what's in the DB
    }

    private suspend fun fetchRepos(page: Int, pageSize: Int) = suspend {
        service.getRepos(page, pageSize)
    }.toResult()
}
