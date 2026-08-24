package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EReports

@Dao
interface DSysReports {

    @Upsert
    fun SaveReport(eReports : EReports)

    @Query("SELECT a.* " +
            "FROM " +
                "Sys_Reports a " +
            "WHERE " +
            "(SELECT " +
                    "nUserLevl & a.nRightsxx " +
            "FROM " +
                    "User_Info " +
            "WHERE " +
                   "sUserIDxx = :fsUserIDxx) > 1")
    fun GetReports(fsUserIDxx : String) : LiveData<List<EReports>>

    @Query("SELECT * FROM Sys_Reports WHERE idxx = :fsIDxx")
    fun GetReportInfo(fsIDxx: String) : LiveData<EReports>
}