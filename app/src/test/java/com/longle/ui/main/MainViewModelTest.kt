package com.longle.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.longle.data.Result
import com.longle.data.model.User
import com.longle.data.repository.UserRepository
import com.longle.ui.main.di.MainActivitySharedData
import com.longle.util.mock
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val sharedData = MainActivitySharedData()
    private val user = Result.success(
        User(
            "google",
            null,
            null,
            null,
            null,
            null
        )
    )
    private val repository = mock(UserRepository::class.java)
    private val viewModel by lazy {
        MainViewModel(sharedData, repository)
    }

    @Before
    fun setup() {
        val result: LiveData<Result<User>> = MutableLiveData(user)
        `when`(repository.getUser()).thenReturn(result)
    }

    @Test
    fun testNull() {
        assertThat(viewModel.user, notNullValue())
    }

    @Test
    fun fetchWhenObserved() {
        val observer = mock<Observer<Result<User>>>()
        viewModel.user.observeForever(observer)
        verify(repository).getUser()
    }

    @Test
    fun testFetchUserSuccess() {
        val observer = mock<Observer<Result<User>>>()
        viewModel.user.observeForever(observer)
        verify(observer).onChanged(user)
    }
}
