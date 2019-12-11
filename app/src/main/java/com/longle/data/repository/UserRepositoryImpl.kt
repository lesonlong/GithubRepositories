package com.longle.data.repository

import com.longle.data.api.RepoService
import com.longle.data.db.AppDatabase
import com.longle.data.resultLiveData
import com.longle.di.ActivityScope
import com.longle.util.RateLimiter
import com.longle.util.toResult
import javax.inject.Inject

/**
 * Repository module for handling data operations.
 */
@ActivityScope
class UserRepositoryImpl
@Inject constructor(
    private val rateLimiter: RateLimiter,
    private val db: AppDatabase,
    private val service: RepoService
) : UserRepository {

    override fun getUser() = resultLiveData(
        databaseQuery = { db.userDao().getUser() },
        shouldFetch = { rateLimiter.shouldFetch(REPOS_KEY_RATE_LIMITER) },
        networkCall = { suspend { service.getUser() }.toResult() },
        saveCallResult = { db.userDao().insert(it) })

    companion object {
        private const val REPOS_KEY_RATE_LIMITER = "REPOS_KEY_RATE_LIMITER"
    }
}
