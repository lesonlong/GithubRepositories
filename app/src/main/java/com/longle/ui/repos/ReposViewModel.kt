package com.longle.ui.repos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.longle.data.repository.ReposRepository
import com.longle.data.repository.ReposRepositoryImpl.Companion.DEFAULT_DB_PAGE_SIZE
import com.longle.di.FragmentScope
import com.longle.ui.main.di.MainActivitySharedData
import com.longle.util.BaseLiveEvent
import javax.inject.Inject

/**
 * The ViewModel for [ReposFragment].
 */
@FragmentScope
class ReposViewModel @Inject constructor(
    private val sharedData: MainActivitySharedData,
    repository: ReposRepository
) : ViewModel() {
    val uiEvent = BaseLiveEvent<MessageEvent>()

    private val reposResult =
        MutableLiveData(repository.getRepos(DEFAULT_DB_PAGE_SIZE, viewModelScope))

    val repos = reposResult.switchMap { it.pagedList }
    val networkState = reposResult.switchMap { it.networkState }
    val refreshState = reposResult.switchMap { it.refreshState }

    fun handleItemPosition(lastPosition: Int) {
        uiEvent.sendEvent { showReposCount(lastPosition, sharedData.publicRepos) }
    }

    fun refresh() {
        reposResult.value?.refresh?.invoke()
    }

    fun retry() {
        reposResult.value?.retry?.invoke()
    }

    interface MessageEvent {
        fun showReposCount(lastPosition: Int, publicRepos: Int)
    }
}
