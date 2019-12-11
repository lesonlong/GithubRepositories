package com.longle.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.longle.data.repository.UserRepository
import com.longle.di.ActivityScope
import com.longle.ui.main.di.MainActivitySharedData
import javax.inject.Inject

/**
 * The ViewModel used in [MainActivity].
 */
@ActivityScope
class MainViewModel @Inject constructor(
    sharedData: MainActivitySharedData,
    repository: UserRepository
) : ViewModel() {

    val user = repository.getUser().map {
        sharedData.publicRepos = it.data?.publicRepos ?: 0
        it
    }
}
