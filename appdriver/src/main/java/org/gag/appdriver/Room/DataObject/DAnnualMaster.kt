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

    @Query("SELECT * FROM Annual_Master WHERE sYearIDxx = :fsYearIDxx")
    fun GetAnnualMaster(fsYearIDxx: String): LiveData<EAnnualMaster>
}