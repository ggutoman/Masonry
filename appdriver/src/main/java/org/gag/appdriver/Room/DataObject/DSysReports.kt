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

    @Query("DELETE FROM Sys_Reports")
    fun Clear()

    @Query("SELECT " +
                        "e.*  " +
                    "FROM " +
                        "Member_Info a " +
                    "JOIN " +
                        "User_Info b " +
                    "ON " +
                        "a.sGLPIDNoX = b.sGLPIDNoX " +
                    "JOIN " +
                        "Officer_Info c " +
                    "ON " +
                        "a.sMemberID = c.sMemberID " +
                    "join  " +
                        "Object_Permissions d  " +
                    "ON " +
                        "d.owner_id = c.sPositnCd " +
                    "OR " +
                        "d.owner_id = c.sMemberID " +
                    "JOIN " +
                        " Sys_Reports e " +
                    "ON " +
                        "e.idxx = d.object_id " +
                    "JOIN " +
                        "Lodge_Calendar f " +
                    "ON " +
                        "c.sYearIDxx = f.sYearIDxx " +
                    "AND " +
                        "f.nYearxxxx= :fnYearxx " +
                    "WHERE " +
                        "b.sUserIDxx = :fsUserIDxx " +
                    "AND " +
                        "d.cRecdStat= '1' " +
                    "AND " +
                        "e.cRecdStat= '1'")
    fun GetReports(fsUserIDxx : String, fnYearxx : String) : LiveData<List<EReports>>

    @Query("SELECT * FROM Sys_Reports WHERE idxx = :fsIDxx")
    fun GetReportInfo(fsIDxx: String) : LiveData<EReports>
}