package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EOfficerHistory

@Dao
interface DOfficerHistory {

    @Upsert(EOfficerHistory::class)
    fun SaveOfficerHistory(eOfficerHistory: EOfficerHistory)

    @Query("DELETE FROM Officer_History")
    fun DeleteOfficerHistory()

    @Query("SELECT * FROM Officer_History WHERE sMemberID= :fsMemberIDx")
    fun GetOfficerHistory(fsMemberIDx : String) : LiveData<List<EOfficerHistory>>
}