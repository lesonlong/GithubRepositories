package com.longle.ui.repodetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.longle.data.model.Repo
import com.longle.data.repository.ReposRepository
import com.longle.util.mock
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class RepoDetailViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repoDetailFragmentArgs = RepoDetailFragmentArgs(12)
    private val repo = Repo(
        12,
        "google",
        null,
        1,
        1,
        1,
        null,
        null,
        "",
        1,
        1
    )
    private val repository = mock(ReposRepository::class.java)
    private val viewModel by lazy {
        RepoDetailViewModel(repoDetailFragmentArgs, repository)
    }

    @Before
    fun setup() {
        val result: LiveData<Repo> = MutableLiveData(repo)
        `when`(repository.getRepo(12)).thenReturn(result)
    }

    @Test
    fun testNull() {
        assertThat(viewModel.repo, notNullValue())
    }

    @Test
    fun fetchWhenObserved() {
        val observer = mock<Observer<Repo>>()
        viewModel.repo.observeForever(observer)
        verify(repository).getRepo(ArgumentMatchers.anyInt())
    }

    @Test
    fun testGetRepoFromDataBaseSuccess() {
        val observer = mock<Observer<Repo>>()
        viewModel.repo.observeForever(observer)
        verify(observer).onChanged(repo)
    }
}
