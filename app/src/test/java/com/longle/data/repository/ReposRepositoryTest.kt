package com.longle.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.longle.data.api.RepoService
import com.longle.data.db.AppDatabase
import com.longle.data.db.RepoDao
import com.longle.data.model.Repo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ReposRepositoryTest {
    private lateinit var repository: ReposRepository
    private val dao = mock(RepoDao::class.java)
    private val service = mock(RepoService::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @Before
    fun setUp() {
        val db = mock(AppDatabase::class.java)
        `when`(db.repoDao()).thenReturn(dao)
        `when`(db.runInTransaction(ArgumentMatchers.any())).thenCallRealMethod()
        repository = ReposRepositoryImpl(db, service)
        `when`(dao.getPagedRepos()).thenReturn(MockDataSourceFactory())
    }

    @Test
    fun loadRepos() {
        runBlocking {
            repository.getRepos(30, coroutineScope = coroutineScope)
            verify(dao).getPagedRepos()
        }
    }

    inner class MockDataSourceFactory : DataSource.Factory<Int, Repo>() {
        override fun create(): DataSource<Int, Repo> {
            return MockDataSource()
        }
    }

    inner class MockDataSource : PageKeyedDataSource<Int, Repo>() {
        override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, Repo>
        ) {
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repo>) {}
        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repo>) {}
    }
}
