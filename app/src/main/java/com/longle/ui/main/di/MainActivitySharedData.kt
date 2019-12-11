package com.longle.ui.main.di

import com.longle.di.ActivityScope
import javax.inject.Inject

@ActivityScope
class MainActivitySharedData @Inject constructor() {
    var publicRepos: Int = 0
}