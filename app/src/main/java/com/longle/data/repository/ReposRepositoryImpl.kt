package com.longle.data.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.longle.data.Result
import com.longle.data.api.RepoService
import com.longle.data.db.AppDatabase
import com.longle.data.model.Repo
import com.longle.di.FragmentScope
import com.longle.util.toResult
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
@FragmentScope
class ReposRepositoryImpl
@Inject constructor(
    private val db: AppDatabase,
    private val service: RepoService
) : ReposRepository {

    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE

    override fun getRepo(id: Int) = db.repoDao().getRepo(id)

    companion object {
        const val DEFAULT_DB_PAGE_SIZE = 50
        private const val DEFAULT_NETWORK_PAGE_SIZE = 30
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertResultIntoDb(repos: List<Repo>) {
        repos.let {
            db.runInTransaction {
                val start = db.repoDao().getNextIndex()
                val pageIndex = db.repoDao().getPageIndex()
                val items = repos.mapIndexed { index, child ->
                    child.indexInResponse = start + index
                    child.pageIndex = pageIndex + 1
                    child
                }
                db.repoDao().insertAll(items)
            }
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(coroutineScope: CoroutineScope): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        coroutineScope.launch(getJobErrorHandler() + Dispatchers.IO) {
            val response = suspend { service.getRepos(1, networkPageSize) }.toResult()
            if (response.status == Result.Status.SUCCESS) {
                db.runInTransaction {
                    db.repoDao().deleteAll()
                    response.data?.let { data ->
                        insertResultIntoDb(data)
                    }
                }
                // since we are in bg thread now, post the result.
                networkState.postValue(NetworkState.LOADED)
            } else if (response.status == Result.Status.ERROR) {
                networkState.value = NetworkState.error(response.message)
            }
        }
        return networkState
    }

    /**
     * Returns a Listing of Repos.
     */
    @MainThread
    override fun getRepos(pageSize: Int, coroutineScope: CoroutineScope): Listing<Repo> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = RepoBoundaryCallback(
            service = service,
            handleResponse = this::insertResultIntoDb,
            networkPageSize = networkPageSize,
            coroutineScope = coroutineScope,
            coroutineExceptionHandler = getJobErrorHandler()
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(coroutineScope)
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = db.repoDao().getPagedRepos().toLiveData(
            pageSize = pageSize,
            boundaryCallback = boundaryCallback
        )

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        Timber.e(e.message ?: e.toString())
    }
}
