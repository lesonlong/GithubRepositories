package com.longle.ui.repodetail.di

import androidx.navigation.fragment.navArgs
import com.longle.data.repository.ReposRepository
import com.longle.data.repository.ReposRepositoryImpl
import com.longle.di.FragmentScope
import com.longle.ui.repodetail.RepoDetailFragment
import com.longle.ui.repodetail.RepoDetailFragmentArgs
import dagger.Module
import dagger.Provides

@Module
class RepoDetailFragmentModule {

    @FragmentScope
    @Provides
    fun provideReposRepository(repo: ReposRepositoryImpl): ReposRepository = repo

    @FragmentScope
    @Provides
    fun provideFragmentArgs(frag: RepoDetailFragment): RepoDetailFragmentArgs {
        return frag.navArgs<RepoDetailFragmentArgs>().value
    }
}
