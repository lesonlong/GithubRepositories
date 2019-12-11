package com.longle.data.api

import com.longle.data.model.Repo
import com.longle.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST API access points
 */
interface RepoService {

    companion object {
        const val ENDPOINT = "https://api.github.com/"
        const val GITHUB_USER = "google"
    }

    @GET("users/$GITHUB_USER")
    suspend fun getUser(): Response<User>

    @GET("users/$GITHUB_USER/repos")
    suspend fun getRepos(
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int
    ): Response<List<Repo>>
}
