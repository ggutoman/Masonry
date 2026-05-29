package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeInfo

@Dao
interface DLodgeInfo {

    @Upsert(ELodgeInfo::class)
    fun SaveLodge(lodge : ELodgeInfo)

    @Query("SELECT * FROM Lodge_Info WHERE sLodgeIDx= :fsLodgeIDx")
    fun GetLodgeInfo(fsLodgeIDx : String): ELodgeInfo
}