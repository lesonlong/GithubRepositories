package com.longle.ui.repos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.Config
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import com.longle.data.model.Repo
import com.longle.data.repository.Listing
import com.longle.data.repository.NetworkState
import com.longle.data.repository.ReposRepository
import com.longle.data.repository.ReposRepositoryImpl.Companion.DEFAULT_DB_PAGE_SIZE
import com.longle.ui.main.di.MainActivitySharedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class ReposViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val listing: Listing<Repo> = Listing(
        pagedList = LivePagedListBuilder(
            MockDataSourceFactory(),
            Config(DEFAULT_DB_PAGE_SIZE)
        ).build(),
        networkState = MutableLiveData(NetworkState.LOADED),
        retry = {
        },
        refresh = {
        },
        refreshState = MutableLiveData(NetworkState.LOADED)
    )

    private val sharedData = MainActivitySharedData()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val repository = mock(ReposRepository::class.java)
    private val viewModel by lazy {
        ReposViewModel(sharedData, repository)
    }

    @Before
    fun setup() {
        `when`(repository.getRepos(DEFAULT_DB_PAGE_SIZE, coroutineScope)).thenReturn(listing)
    }

    @Test
    fun testNull() {
        assertThat(viewModel.repos, notNullValue())
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
