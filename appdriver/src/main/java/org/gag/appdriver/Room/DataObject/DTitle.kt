package org.gag.appdriver.Room.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.gag.appdriver.Room.Entities.ETitle

@Dao
interface DTitle {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertTitle(title: ETitle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertTitle(title: List<ETitle>)

    @Query("""
        SELECT * 
        FROM Title 
        WHERE cRecdStat = '1'
    """)
    fun GetTitles(): LiveData<List<ETitle>>

    @Query("""
        SELECT * 
        FROM Title 
        WHERE sTitleIDx = :titleID
    """)
    fun GetTitle(titleID: String): LiveData<ETitle>

    @Query("DELETE FROM Title")
    suspend fun DeleteTitles()
}