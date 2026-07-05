package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EReceiptDetail
import org.gag.appdriver.Room.Entities.EReceiptMaster

@Dao
interface DReceipt {

    @Upsert(entity = EReceiptMaster::class)
    fun SaveReceiptMaster(foMaster : EReceiptMaster)

    @Upsert(entity = EReceiptDetail::class)
    fun SaveReceiptDetail(foDetail : EReceiptDetail)

    @Query("DELETE FROM Receipts_Detail WHERE sTransNox= :fsTransNox")
    fun DeleteReceiptDetails(fsTransNox : String)

    @Query("SELECT * FROM Receipts_Master WHERE sTransNox= :fsTransNox")
    fun ObserveReceiptMaster(fsTransNox : String) : LiveData<EReceiptMaster>

    @Query("SELECT * FROM Receipts_Detail WHERE sTransNox= :fsTransNox")
    fun ObserveReceiptDetail(fsTransNox : String) : LiveData<List<EReceiptDetail>>
}