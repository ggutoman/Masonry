package org.gag.appdriver.Room.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.gag.appdriver.Room.Entities.ELodge

@Dao
interface DLodge {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertLodge(lodge: ELodge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun InsertLodge(lodge: List<ELodge>)

    @Query("""
        SELECT * 
        FROM xxxLodgeInfo
    """)
    fun GetLodges(): LiveData<List<ELodge>>

    @Query("""
        SELECT * 
        FROM xxxLodgeInfo
        WHERE sLodgeIDx = :lodgeID
    """)
    fun GetLodge(lodgeID: String): LiveData<ELodge>

    @Query("DELETE FROM xxxLodgeInfo")
    fun DeleteLodges()
}