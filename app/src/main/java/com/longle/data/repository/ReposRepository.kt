package com.longle.data.repository

import androidx.lifecycle.LiveData
import com.longle.data.model.Repo
import kotlinx.coroutines.CoroutineScope

interface ReposRepository {

    fun getRepo(id: Int): LiveData<Repo>

    fun getRepos(pageSize: Int, coroutineScope: CoroutineScope): Listing<Repo>
}
