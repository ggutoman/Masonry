package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.LodgeFundMasterList
import org.gag.appdriver.Room.Entities.EFundMaster

@Dao
interface DFundMaster {

    @Upsert
    fun SaveFundMaster(eFund: EFundMaster)

    @Query("SELECT a.sLodgeIDx sLodgeIDx, b.sLodgeNme sLodgeNme, a.nBalancex nBalancex FROM Lodge_Fund_Master a JOIN Lodge_Info b ON a.sLodgeIDx = b.sLodgeIDx WHERE a.sLodgeIDx = :fsLodgeIDxx")
    fun ObserveLodgeFunds(fsLodgeIDxx : String) : LiveData<List<LodgeFundMasterList>>

    @Query("DELETE FROM Lodge_Fund_Master")
    fun DeleteFunds()

}