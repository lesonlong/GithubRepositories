package com.longle.data.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.longle.data.model.Repo

/**
 * The Data Access Object for the Repo class.
 */
@Dao
interface RepoDao {

    @Query("SELECT * FROM Repo ORDER BY indexInResponse ASC")
    fun getPagedRepos(): DataSource.Factory<Int, Repo>

    @Query("SELECT * FROM Repo WHERE id = :id")
    fun getRepo(id: Int): LiveData<Repo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(repos: List<Repo>)

    @Query("DELETE FROM Repo")
    fun deleteAll()

    @Query("SELECT MAX(indexInResponse) + 1 FROM Repo")
    fun getNextIndex(): Int

    @Query("SELECT MAX(pageIndex) FROM Repo")
    fun getPageIndex(): Int
}
