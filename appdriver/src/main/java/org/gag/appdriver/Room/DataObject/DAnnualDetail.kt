package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.App.Models.AnnualSummary
import org.gag.appdriver.Room.Entities.EAnnualDetail

@Dao
interface DAnnualDetail {

    @Upsert
    fun SaveAnnualDetail(eAnnualDetail: EAnnualDetail)

    @Query("DELETE FROM Annual_Detail")
    fun Clear()

    @Query("SELECT a.sTransNox, " +
                            "a.sMemberID, " +
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
                            "a.sTransNox = :fsTransNox " +
                    "ORDER BY " +
                            "a.nAmtDuexx DESC")
    fun GetAnnualDetail(fsTransNox: String): LiveData<List<AnnualMembers>>

    @Query("SELECT " +
                        "a.sTransNox, " +
                        "c.sMemberID, " +
                        "(c.sFrstName || ' ' || c.sLastName) sMemberNme," +
                        "b.cExemptID cExemptID," +
                        "b.nAmtDuexx nAmtDuexx," +
                        "b.nAmtPaidx nAmtPaidx " +
                    "FROM " +
                        "Annual_Master a " +
                    "JOIN " +
                        "Annual_Detail b " +
                    "ON " +
                        "a.sTransNox = b.sTransNox " +
                    "JOIN " +
                        "Member_Info c " +
                    "ON " +
                        "(b.sMemberID = c.sMemberID) " +
                    "WHERE " +
                        "b.sMemberID = :fsMemberIDxx " +
                    "AND " +
                        "b.nAmtDuexx > 0 " +
                    "GROUP BY " +
                        "a.sTransNox " +
                    "ORDER BY " +
                        "b.nAmtDuexx " +
                    "DESC")
    fun GetAnnualMemberDetail(fsMemberIDxx: String): LiveData<List<AnnualMembers>>

    @Query("SELECT " +
            "a.sTransNox, " +
            "c.nYearxxxx nYearxx, " +
            "b.dDueDatex dDueDate," +
            "SUM(a.nAmtDuexx) nTotalTrans, " +
            "SUM(a.nAmtPaidx) nTotalColl " +
            "FROM " +
            "Annual_Detail a " +
            "JOIN " +
            "Annual_Master b " +
            "ON " +
            "a.sTransNox = b.sTransNox " +
            "JOIN " +
            "Lodge_Calendar c " +
            "ON " +
            "b.sYearIDxx = c.sYearIDxx "+
            "JOIN " +
            "Member_Info d " +
            "ON " +
            "a.sMemberID = d.sMemberID "+
            "JOIN " +
            "User_Info e " +
            "ON " +
            "d.sGLPIDNoX = e.sGLPIDNoX "+
            "WHERE " +
            "e.sUserIDxx= :fsUserIDxx " +
            "GROUP BY " +
            "a.sTransNox")
    fun GetAnnualMemberInfo(fsUserIDxx: String): LiveData<List<AnnualSummary>>

    @Query("SELECT " +
                        "a.sTransNox, " +
                        "c.nYearxxxx nYearxx, " +
                        "b.dDueDatex dDueDate," +
                        "SUM(a.nAmtDuexx) nTotalTrans, " +
                        "SUM(a.nAmtPaidx) nTotalColl " +
                    "FROM " +
                        "Annual_Detail a " +
                    "JOIN " +
                        "Annual_Master b " +
                    "ON " +
                        "a.sTransNox = b.sTransNox " +
                    "JOIN " +
                        "Lodge_Calendar c " +
                    "ON " +
                        "b.sYearIDxx = c.sYearIDxx "+
                    "WHERE " +
                        "a.sTransNox= :fsTransNox " +
                    "GROUP BY " +
                        "a.sTransNox")
    fun GetAnnualSummary(fsTransNox: String): AnnualSummary
}