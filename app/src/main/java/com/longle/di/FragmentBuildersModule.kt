package com.longle.di

import com.longle.ui.repodetail.RepoDetailFragment
import com.longle.ui.repodetail.di.RepoDetailFragmentModule
import com.longle.ui.repos.ReposFragment
import com.longle.ui.repos.di.ReposFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [ReposFragmentModule::class])
    abstract fun contributeReposFragment(): ReposFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RepoDetailFragmentModule::class])
    abstract fun contributeRepoDetailFragment(): RepoDetailFragment
}
