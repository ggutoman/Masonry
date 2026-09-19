package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ESysNotifs

@Dao
interface DSysNotif {

    @Upsert
    fun SaveNotif(eNotif: ESysNotifs)

    @Query("DELETE FROM xxSysNotifxx")
    fun Clear()

    @Query("SELECT * FROM xxSysNotifxx WHERE cReadxxxx = '0' AND sRcvrIDxx= :fsRcvr")
    fun GetUnreadNotifs(fsRcvr : String) : LiveData<List<ESysNotifs>>
}