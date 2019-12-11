package com.longle.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["id"])
data class Repo(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("full_name")
    val title: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("stargazers_count")
    val stars: Int,
    @field:SerializedName("watchers_count")
    val watchersCount: Int,
    @field:SerializedName("forks_count")
    val forksCount: Int,
    @field:SerializedName("language")
    val language: String?,
    @field:SerializedName("updated_at")
    val updatedAt: String?,
    @field:SerializedName("html_url")
    val html_url: String,
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1,
    // to be page index for call api
    var pageIndex: Int = -1
)