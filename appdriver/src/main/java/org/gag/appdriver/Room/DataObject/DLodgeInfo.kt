package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeInfo

@Dao
interface DLodgeInfo {

    @Upsert(ELodgeInfo::class)
    fun SaveLodge(lodge : ELodgeInfo)

    @Query("DELETE FROM Lodge_Info")
    fun DeleteLodge()

    @Query("SELECT * FROM Lodge_Info WHERE sLodgeIDx= :fsLodgeIDx")
    fun GetLodgeInfo(fsLodgeIDx : String): ELodgeInfo

    @Query("SELECT * FROM Lodge_Info WHERE sLodgeIDx= :fsLodgeIDx")
    fun ObserveLodgeInfo(fsLodgeIDx : String): LiveData<ELodgeInfo>

    @Query("SELECT * FROM Lodge_Info")
    fun ObserveLodgeList() : LiveData<List<ELodgeInfo>>
}