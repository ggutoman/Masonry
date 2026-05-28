package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EUserInfo

@Dao
interface DUserInfo {

    @Upsert(entity = EUserInfo::class)
    fun SaveUserInfo(poUser: EUserInfo)

    @Query("SELECT * FROM User_Info")
    fun GetUser(): LiveData<EUserInfo>

    @Query("DELETE FROM User_Info")
    fun DeleteUser()

}