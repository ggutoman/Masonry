package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.Room.Entities.EAnnualDetail

@Dao
interface DAnnualDetail {

    @Upsert
    fun SaveAnnualDetail(eAnnualDetail: EAnnualDetail)

    @Query("SELECT a.sMemberID, " +
                            "(b.sFrstName || ' ' || b.sLastName) sMemberNme," +
                            "a.cExemptID cExemptID," +
                            "a.sRemarksx sRemarksx," +
                            "a.nAmtDuexx nAmtDuexx," +
                            "a.nAmtPaidx nAmtPaidx " +
                    "FROM " +
                            "Annual_Detail a " +
                    "JOIN " +
                            "Member_Info b " +
                    "ON " +
                            "(a.sMemberID = b.sMemberID) " +
                    "WHERE " +
                            "a.sTransNox = :fsTransNox")
    fun GetAnnualDetail(fsTransNox: String): LiveData<List<AnnualMembers>>
}