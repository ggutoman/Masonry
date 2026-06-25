package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EAnnualMaster

@Dao
interface DAnnualMaster {

    @Upsert
    fun SaveAnnualMaster(eAnnual: EAnnualMaster)

    @Query("DELETE FROM Annual_Master")
    fun Clear()

    @Query("SELECT * FROM Annual_Master WHERE sYearIDxx = :fsYearIDxx")
    fun GetAnnualMaster(fsYearIDxx: String): LiveData<EAnnualMaster>

    @Query("SELECT " +
                        "* " +
                    "FROM " +
                        "Annual_Master a " +
                    "JOIN " +
                        "Lodge_Calendar b " +
                    "ON " +
                        "(a.sYearIDxx = b.sYearIDxx) " +
                    "AND " +
                        ":fsLodgeIDxx = b.sLodgeIDx " +
                    "AND " +
                        "a.dDueDatex " +
                    "BETWEEN " +
                        ":fsYearFrom " +
                    "AND " +
                        ":fsYearTo " +
                    "GROUP BY " +
                        "a.sYearIDxx ")
    fun GetAnnualSummary(fsLodgeIDxx: String, fsYearFrom : String, fsYearTo : String): LiveData<List<EAnnualMaster>>
}