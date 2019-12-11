package com.longle.ui.repos.di

import com.longle.data.repository.ReposRepository
import com.longle.data.repository.ReposRepositoryImpl
import com.longle.di.FragmentScope
import dagger.Module
import dagger.Provides

@Module
class ReposFragmentModule {

    @FragmentScope
    @Provides
    fun provideReposRepository(repo: ReposRepositoryImpl): ReposRepository = repo
}
