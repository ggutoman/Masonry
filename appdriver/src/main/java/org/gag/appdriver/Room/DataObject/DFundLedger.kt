package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EFundLedger

@Dao
interface DFundLedger {

    @Upsert
    fun SaveFundLedger(eFund: EFundLedger)

    @Query("DELETE FROM Lodge_Fund_Ledger")
    fun DeleteLedger()

    @Query("select * from lodge_fund_ledger where sLodgeIDx = :fsLodgeIDxx AND dTransact BETWEEN :fsDfromx AND :fsDTox")
    fun ObserveLedgers(fsLodgeIDxx: String, fsDfromx: String, fsDTox: String): LiveData<List<EFundLedger>>

}