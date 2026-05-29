package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.ETitle

@Dao
interface DTitleInfo {

    @Upsert(ETitle::class)
    fun SaveTitle(title : ETitle)

    @Query("SELECT * FROM Title_Info")
    fun ObserveTitleList() : LiveData<List<ETitle>>
}