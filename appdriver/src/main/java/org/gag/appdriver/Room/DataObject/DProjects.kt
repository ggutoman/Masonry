package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.ProjectDetail
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster

@Dao
interface DProjects {

    @Upsert(entity = EProjectMaster::class)
    fun SaveMaster(projectMaster : EProjectMaster)

    @Upsert(entity = EProjectDetail::class)
    fun SaveDetail(projectDetail : EProjectDetail)

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

    @Query("SELECT * FROM Project_Master WHERE sYearIDxx= :fsLodgeIDx AND dDueDatex BETWEEN :fsDfrom AND :fsDto")
    fun GetProjectMasterList(fsLodgeIDx : String, fsDfrom : String, fsDto : String) : LiveData<List<EProjectMaster>>

}