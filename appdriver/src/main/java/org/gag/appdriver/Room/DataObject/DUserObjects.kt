package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EUserObjects

@Dao
interface DUserObjects {

    @Upsert(EUserObjects::class)
    fun SaveObjects(eUserObjects: EUserObjects)

    @Query("DELETE FROM User_Objects")
    fun ClearObjects()

    @Query("SELECT * FROM User_Objects WHERE (:fnUsrLevel & nRights) > 0 AND cRecdStat = '1' ORDER BY idxx ASC, sParentIDxx ASC")
    fun GetUserObjects(fnUsrLevel : Int) : LiveData<List<EUserObjects>>

    @Query("DELETE FROM User_Objects")
    fun Clear()
}