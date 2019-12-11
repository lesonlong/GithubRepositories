package com.longle.ui.main.di

import com.longle.data.repository.UserRepository
import com.longle.data.repository.UserRepositoryImpl
import com.longle.di.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @ActivityScope
    @Provides
    fun provideUserRepository(repo: UserRepositoryImpl): UserRepository = repo
}
