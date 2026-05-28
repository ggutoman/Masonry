package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContact
import org.gag.appdriver.Room.Entities.EMemberEmail
import org.gag.appdriver.Room.Entities.EMemberMaster
import org.gag.appdriver.Room.Relations.RMemberFullInfo

@Dao
interface DLodgeInfo {

    @Upsert(ELodgeInfo::class)
    fun SaveLodge(lodge : ELodgeInfo)

    @Query("SELECT * FROM Lodge_Info WHERE sLodgeIDx= :fsLodgeIDx")
    fun GetLodgeInfo(fsLodgeIDx : String): ELodgeInfo
}