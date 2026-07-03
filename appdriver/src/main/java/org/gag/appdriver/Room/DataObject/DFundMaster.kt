package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EFundMaster
import org.gag.appdriver.Room.Entities.EFundTurnOver

@Dao
interface DFundMaster {

    @Upsert
    fun SaveFundMaster(eFund: EFundMaster)

    @Query("DELETE FROM Lodge_Fund_Master")
    fun DeleteFunds()

}