package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.ReceiptMasterList
import org.gag.appdriver.Room.Entities.EReceiptDetail
import org.gag.appdriver.Room.Entities.EReceiptMaster

@Dao
interface DReceipt {

    @Upsert(entity = EReceiptMaster::class)
    fun SaveReceiptMaster(foMaster : EReceiptMaster)

    @Upsert(entity = EReceiptDetail::class)
    fun SaveReceiptDetail(foDetail : EReceiptDetail)

    @Query("DELETE FROM Receipts_Master")
    fun DeleteReceiptMaster()

    @Query("DELETE FROM Receipts_Detail")
    fun DeleteReceiptDetails()

    @Query("DELETE FROM Receipts_Detail WHERE sTransNox= :fsTransNox")
    fun DeleteReceiptDetails(fsTransNox : String)

    @Query("SELECT * FROM Receipts_Master WHERE sTransNox= :fsTransNox")
    fun ObserveReceiptMaster(fsTransNox : String) : LiveData<EReceiptMaster>

    @Query("SELECT * FROM Receipts_Detail WHERE sTransNox= :fsTransNox")
    fun ObserveReceiptDetail(fsTransNox : String) : LiveData<List<EReceiptDetail>>

    @Query("SELECT " +
                        "a.sTransNox sTransNox, " +
                        "a.sMemberID sMemberID, " +
                        "(b.sFrstName || ' ' || b.sLastName) sMemberNme, " +
                        "a.dTransact dTransact, " +
                        "a.cPaymForm cPaymForm, " +
                        "a.sORNoxxxx sORNoxxxx, " +
                        "a.cSendStat cSendStat, " +
                        "a.cTranStat cTranStat, " +
                        "a.nTotlPaid nTotlPaid " +
                    "FROM " +
                        "Receipts_Master a " +
                    "JOIN " +
                        "Member_Info b " +
                    "ON " +
                        "a.sMemberID = b.sMemberID " +
                    "WHERE " +
                        "a.dTransact " +
                    "BETWEEN " +
                        ":fDfrom " +
                    "AND " +
                        ":fDto")
    fun ObserveReceiptMasterList(fDfrom : String, fDto : String) : LiveData<List<ReceiptMasterList>>
}