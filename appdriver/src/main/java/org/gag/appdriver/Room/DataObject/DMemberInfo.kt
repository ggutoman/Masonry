package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EUserInfo

@Dao
interface DMemberInfo {

    @Upsert(entity = EMemberInfo::class)
    fun SaveMemberInfo(poMember: EMemberInfo)

    @Query("SELECT * FROM Member_Info")
    fun GetMember(): LiveData<EMemberInfo>

    @Query("DELETE FROM Member_Info")
    fun DeleteMember()
}