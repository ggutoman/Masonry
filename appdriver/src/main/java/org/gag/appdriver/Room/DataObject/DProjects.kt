package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.MemberPledges
import org.gag.appdriver.App.Models.ProjectDetail
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster

@Dao
interface DProjects {

    @Upsert(entity = EProjectMaster::class)
    fun SaveMaster(projectMaster : EProjectMaster)

    @Upsert(entity = EProjectDetail::class)
    fun SaveDetail(projectDetail : EProjectDetail)

    @Query("DELETE FROM Project_Detail WHERE sProjctCd= :fsProjectCd")
    fun DeleteDetail(fsProjectCd : String)

    @Query("DELETE FROM Project_Master")
    fun DeleteMaster()

    @Query("DELETE FROM Project_Detail")
    fun DeleteDetail()

    @Query("SELECT * FROM Project_Master WHERE sProjctCd= :fsProjectCd")
    fun GetProjectMasterTransaction(fsProjectCd : String) : LiveData<EProjectMaster>

    @Query("SELECT " +
                            "a.sProjctCd sProjectCd, " +
                            "a.sMemberID sMemberID, " +
                            "(b.sFrstName || ' ' || b.sLastName) sMemberNme, " +
                            "a.sORNoxxxx sORNoxxxx, " +
                            "a.dPledgexx dPledgexx, " +
                            "a.nPledgexx nPledgexx, " +
                            "a.nAmtPaidx nAmtPaidx " +
                    "FROM " +
                            "Project_Detail a " +
                    "JOIN " +
                        "Member_Info b " +
                    "ON " +
                        "a.sMemberID = b.sMemberID " +
                    "WHERE " +
                        "a.sProjctCd= :fsProjectCd")
    fun GetProjectDetails(fsProjectCd : String) : LiveData<List<ProjectDetail>>

    @Query("SELECT " +
            "a.sProjctCd sProjectCd, " +
            "a.sMemberID sMemberID, " +
            "(b.sFrstName || ' ' || b.sLastName) sMemberNme, " +
            "a.sORNoxxxx sORNoxxxx, " +
            "a.dPledgexx dPledgexx, " +
            "a.nPledgexx nPledgexx, " +
            "a.nAmtPaidx nAmtPaidx " +
            "FROM " +
            "Project_Detail a " +
            "JOIN " +
            "Member_Info b " +
            "ON " +
            "a.sMemberID = b.sMemberID " +
            "WHERE " +
            "a.sProjctCd= :fsProjectCd " +
            "AND " +
            "a.dPledgexx " +
            "BETWEEN " +
            ":fsDfrom " +
            "AND " +
            ":fsDto")
    fun GetPledgeSummary(fsProjectCd : String, fsDfrom : String, fsDto : String) : LiveData<List<ProjectDetail>>

    @Query("SELECT " +
                        "a.sProjctCd sProjctCd, " +
                        "a.sProjctNm sProjctNm, " +
                        "a.cTranStat cTranStat, " +
                        "b.dPledgexx dPledgexx, " +
                        "b.nPledgexx nPledgexx, " +
                        "b.sORNoxxxx sORNoxxxx, " +
                        "b.nAmtPaidx nAmtPaidx " +
                    "FROM " +
                        "Project_Master a " +
                    "JOIN " +
                        "Project_Detail b " +
                    "ON " +
                        "(a.sProjctCd = b.sProjctCd) " +
                    "WHERE " +
                        "b.sMemberID= :fsMemberIDxx " +
                    "AND " +
                        "dDueDatex " +
                    "BETWEEN " +
                        ":fsDfrom " +
                    "AND " +
                        ":fsDto " +
                    "GROUP BY " +
                        "a.sProjctCd, b.dPledgexx " +
                    "ORDER BY " +
                        "b.dPledgexx " +
                    "DESC")
    fun GetMemberPledges(fsMemberIDxx : String, fsDfrom : String, fsDto : String) : LiveData<List<MemberPledges>>

    @Query("SELECT " +
                        "a.sProjctCd sProjctCd, " +
                        "a.sProjctNm sProjctNm, " +
                        "a.cTranStat cTranStat, " +
                        "b.dPledgexx dPledgexx, " +
                        "b.nPledgexx nPledgexx, " +
                        "b.sORNoxxxx sORNoxxxx, " +
                        "b.nAmtPaidx nAmtPaidx " +
                    "FROM " +
                        "Project_Master a " +
                    "JOIN " +
                        "Project_Detail b " +
                    "ON " +
                        "(a.sProjctCd = b.sProjctCd) " +
                    "WHERE " +
                        "b.sMemberID= :fsMemberIDxx " +
                    "AND " +
                        "b.nAmtPaidx > 0 " +
                    "GROUP BY " +
                        "a.sProjctCd, b.dPledgexx " +
                    "ORDER BY " +
                        "b.dPledgexx " +
                    "DESC")
    fun GetMemberPledges(fsMemberIDxx : String) : LiveData<List<MemberPledges>>

    @Query("SELECT * FROM Project_Master WHERE sYearIDxx= :fsYearIDxx AND dDueDatex BETWEEN :fsDfrom AND :fsDto")
    fun GetProjectMasterList(fsYearIDxx : String, fsDfrom : String, fsDto : String) : LiveData<List<EProjectMaster>>

    @Query("SELECT * FROM Project_Master a JOIN Lodge_Calendar b ON (a.sYearIDxx = b.sYearIDxx) WHERE b.sLodgeIDx= :fsLodgeIDxx GROUP BY a.sProjctCd")
    fun GetLodgeProjects(fsLodgeIDxx : String) : LiveData<List<EProjectMaster>>

}