package com.longle.ui.repodetail

import androidx.lifecycle.ViewModel
import com.longle.data.repository.ReposRepository
import com.longle.di.FragmentScope
import javax.inject.Inject

/**
 * The ViewModel used in [RepoDetailFragment].
 */
@FragmentScope
class RepoDetailViewModel @Inject constructor(
    args: RepoDetailFragmentArgs,
    repository: ReposRepository
) : ViewModel() {

    val repo by lazy { repository.getRepo(args.id) }
}
