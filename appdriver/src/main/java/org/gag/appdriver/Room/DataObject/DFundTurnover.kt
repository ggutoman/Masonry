package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EFundTurnOver

@Dao
interface DFundTurnover {

    @Upsert
    fun SaveTurnover(eTunover: EFundTurnOver)

    @Query("SELECT * FROM Fund_Turnover WHERE sTransNox= :fsTransNox")
    fun ObserveTurnover(fsTransNox : String): LiveData<EFundTurnOver>

    @Query("SELECT * FROM Fund_Turnover WHERE sYearIDxx= :fsYearIDxx AND  ((dTransact BETWEEN :fsDfrom AND :fsDto) OR (dApproved BETWEEN :fsDfrom AND :fsDto)) ")
    fun ObserveTurnoverList( fsYearIDxx : String, fsDfrom : String, fsDto : String): LiveData<List<EFundTurnOver>>

    @Query("DELETE FROM Fund_Turnover")
    fun DeleteTurnover()

}