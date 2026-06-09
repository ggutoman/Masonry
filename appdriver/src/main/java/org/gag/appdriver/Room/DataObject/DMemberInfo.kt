package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EMemberInfo

@Dao
interface DMemberInfo {

    @Upsert(entity = EMemberInfo::class)
    fun SaveMemberInfo(poMember: EMemberInfo)

    @Query("SELECT a.sGLPIDNoX, a.sLastName, a.sFrstName, a.sMiddName, a.sSuffixNm, a.dBirthDte, a.sMemberID, d.sYearIDxx, b.sLodgeNme " +
                    "FROM Member_Info a LEFT JOIN Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx) LEFT JOIN Position_Info c ON (a.sPositnCd = c.sPositnCd) " +
                    "LEFT JOIN Officer_Info d ON (a.sMemberID = d.sMemberID) " +
                    "WHERE sGLPIDNoX= (" +
                    "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun ObserveMemberInfoByUserID(fsUserIDx : String): LiveData<MemberDashboardInfo>

    @Query("SELECT a.sGLPIDNoX, a.sLastName, a.sFrstName, a.sMiddName, a.sSuffixNm, a.dBirthDte, a.sMemberID, d.sYearIDxx, b.sLodgeNme " +
            "FROM Member_Info a LEFT JOIN Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx) LEFT JOIN Position_Info c ON (a.sPositnCd = c.sPositnCd) " +
            "LEFT JOIN Officer_Info d ON (a.sMemberID = d.sMemberID) " +
            "WHERE sGLPIDNoX= (" +
            "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun GetMemberParameters(fsUserIDx : String): MemberDashboardInfo

    @Query("SELECT * FROM Member_Info WHERE sMemberID != :fsMemberIDx AND dMembrshp BETWEEN :fsDateFrom AND :fsDateTo ORDER BY dMembrshp DESC, sGLPIDNoX ASC")
    fun ObserveMemberListByFilter(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String): LiveData<List<EMemberInfo>>

    @Query("SELECT * FROM Member_Info  ORDER BY dMembrshp DESC")
    fun ObserveMemeberList(): LiveData<List<EMemberInfo>>

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= (" +
            "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun GetMemberInfoByUserID(fsUserIDx : String): EMemberInfo

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= :fsGLPIDxx")
    fun GetMemberInfoByGLPID(fsGLPIDxx : String): LiveData<EMemberInfo>

    @Query("DELETE FROM Member_Info")
    fun DeleteMember()

    data class MemberDashboardInfo(
        val sGLPIDNoX : String?,
        val sLastName : String?,
        val sFrstName : String?,
        val sMiddName : String?,
        val sSuffixNm : String?,
        val dBirthDte : String?,
        val sMemberID : String?,
        val sYearIDxx: String?,
        val sLodgeNme: String?,
    )
}