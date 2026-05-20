package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EUserInfo

@Dao
interface DUserInfo {

    @Upsert(entity = EUserInfo::class)
    fun SaveUserInfo(poUser: EUserInfo)
}