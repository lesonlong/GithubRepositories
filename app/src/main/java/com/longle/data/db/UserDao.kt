package com.longle.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.longle.data.model.User

/**
 * The Data Access Object for the User class.
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM User LIMIT 1")
    fun getUser(): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)
}
