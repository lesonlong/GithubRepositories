package com.longle.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["login"])
data class User(
    @field:SerializedName("login")
    val login: String,
    @field:SerializedName("avatar_url")
    val avatarUrl: String?,
    @field:SerializedName("name")
    val name: String?,
    @field:SerializedName("email")
    val email: String?,
    @field:SerializedName("html_url")
    val htmlUrl: String?,
    @field:SerializedName("public_repos")
    val publicRepos: Int?
)