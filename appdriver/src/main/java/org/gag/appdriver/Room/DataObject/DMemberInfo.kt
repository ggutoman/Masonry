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

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= (" +
            "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun ObserveMemberInfoByUserID(fsUserIDx : String): LiveData<EMemberInfo>

    @Query("SELECT * FROM Member_Info WHERE sGLPIDNoX= (" +
            "SELECT sGLPIDNoX FROM User_Info WHERE sUserIDxx= :fsUserIDx)")
    fun GetMemberInfoByUserID(fsUserIDx : String): EMemberInfo

    @Query("DELETE FROM Member_Info")
    fun DeleteMember()
}