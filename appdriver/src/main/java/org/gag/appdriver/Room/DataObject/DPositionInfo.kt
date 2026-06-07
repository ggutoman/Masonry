package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EPosition

@Dao
interface DPositionInfo {

    @Upsert(EPosition::class)
    fun SavePosition(position : EPosition)

    @Query("SELECT * FROM Position_Info WHERE sPositnCd= :fspositionCd")
    fun GetLodgeInfo(fspositionCd : String): EPosition

    @Query("SELECT * FROM Position_Info")
    fun ObserverPositionList() : LiveData<List<EPosition>>
}