package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.OfficerHistory
import org.gag.appdriver.Room.Entities.EOfficerHistory

@Dao
interface DOfficerHistory {

    @Upsert(EOfficerHistory::class)
    fun SaveOfficerHistory(eOfficerHistory: EOfficerHistory)

    @Query("DELETE FROM Officer_History")
    fun DeleteOfficerHistory()

    @Query("SELECT " +
            "a.sTransNox," +
            "a.sYearIDxx," +
            "b.sMemberID," +
            "c.nYearxxxx, " +
            "(b.sFrstName || ' ' || b.sLastName) sMemberNme," +
            "a.dTransact," +
            "a.cOldStatx," +
            "a.cNewStatx," +
            "d.sPositnDs " +
            "FROM Officer_History a " +
            "JOIN Member_Info b ON a.sMemberID = b.sMemberID " +
            "JOIN Lodge_Calendar c ON a.sYearIDxx = c.sYearIDxx " +
            "JOIN  Position_Info d ON (a.sPositnCd = d.sPositnCd) " +
            "WHERE (b.sGLPIDNoX= :fsMemberIDx) " +
            "AND a.dTransact " +
            "BETWEEN :fsDateFrom AND :fsDateTo " +
            "GROUP BY a.sTransNox ORDER BY a.dTransact DESC")
    fun ObserveOfficerHistory(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<OfficerHistory>>
}