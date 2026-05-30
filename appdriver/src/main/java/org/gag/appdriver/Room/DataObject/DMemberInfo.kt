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

    @Query("SELECT a.sMemberNm, b.sLodgeNme, c.sPositnCd " +
                    "FROM Member_Info a LEFT JOIN Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx) LEFT JOIN Position_Info c ON (a.sPositnCd = c.sPositnCd) " +
                    "WHERE sGLPIDNoX= (" +
                    "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun ObserveMemberInfoByUserID(fsUserIDx : String): LiveData<MemberDashboardInfo>

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= (" +
            "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun GetMemberInfoByUserID(fsUserIDx : String): EMemberInfo

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= :fsGLPIDxx")
    fun GetMemberInfoByGLPID(fsGLPIDxx : String): LiveData<EMemberInfo>

    @Query("DELETE FROM Member_Info")
    fun DeleteMember()

    data class MemberDashboardInfo(
        val sMemberNm: String,
        val sLodgeNme: String
    )
}