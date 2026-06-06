package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EMemberEmailInfo

@Dao
interface DMemberEmailInfo {

    @Upsert(entity = EMemberEmailInfo::class)
    fun SaveMemberEmail(poContact: EMemberEmailInfo)

    @Query("SELECT * FROM Member_Email WHERE sMemberID = :fsMemberID")
    fun GetMemberEmail(fsMemberID : String): LiveData<List<EMemberEmailInfo>>

}