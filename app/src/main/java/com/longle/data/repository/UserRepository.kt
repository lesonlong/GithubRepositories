package com.longle.data.repository

import androidx.lifecycle.LiveData
import com.longle.data.Result
import com.longle.data.model.User

interface UserRepository {

    fun getUser(): LiveData<Result<User>>
}
