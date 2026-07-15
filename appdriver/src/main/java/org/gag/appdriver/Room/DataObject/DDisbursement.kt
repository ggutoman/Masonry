package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.App.Models.AnnualSummary
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EDisbursement
import org.gag.appdriver.Room.Entities.EDisbursementAttachment

@Dao
interface DDisbursement {

    @Upsert
    fun SaveDisbursement(eDDisbursement: EDisbursement)

    @Upsert
    fun SaveAttachment(eAttachment : EDisbursementAttachment)

    @Query("DELETE FROM Disbursement")
    fun DeleteDisbursements()

    @Query("SELECT * FROM Disbursement WHERE sTransNox= :fsTransNox")
    fun GetDisbursement(fsTransNox : String) : LiveData<EDisbursement>

    @Query("SELECT * FROM Disbursement WHERE dTransact BETWEEN :fDfromxx AND :fDtoxx")
    fun GetDisbursementList(fDfromxx : String, fDtoxx : String) : LiveData<List<EDisbursement>>

    @Query("SELECT * FROM DV_Attachment WHERE sSourceNo= :fsSourceNo")
    fun GetAttachments(fsSourceNo : String) : LiveData<List<EDisbursementAttachment>>

}