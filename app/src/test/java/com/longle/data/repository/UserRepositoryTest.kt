package com.longle.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.longle.data.api.RepoService
import com.longle.data.db.AppDatabase
import com.longle.data.db.UserDao
import com.longle.util.CoroutineTestRule
import com.longle.util.RateLimiter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class UserRepositoryTest {
    private lateinit var repository: UserRepository
    private val rateLimiter = mock(RateLimiter::class.java)
    private val dao = mock(UserDao::class.java)
    private val service = mock(RepoService::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        val db = mock(AppDatabase::class.java)
        `when`(db.userDao()).thenReturn(dao)
        `when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = UserRepositoryImpl(rateLimiter, db, service)
    }

    @Test
    fun loadUser() {
//        val observer = mock<Observer<Result<User>>>()
//        repository.getUser().observeForever(observer)
//        verify(dao).getUser()
    }
}
