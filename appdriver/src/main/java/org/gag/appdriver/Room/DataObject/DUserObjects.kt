package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EUserObjects

@Dao
interface DUserObjects {

    @Upsert
    fun SaveObjects(eUserObjects: EUserObjects)

    @Query("DELETE FROM User_Objects")
    fun Clear()
}