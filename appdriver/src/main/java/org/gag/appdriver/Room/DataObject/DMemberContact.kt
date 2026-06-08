package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EMemberContactInfo

@Dao
interface DMemberContact {

    @Upsert(entity = EMemberContactInfo::class)
    fun SaveMemberContact(poContact: EMemberContactInfo)

    @Query("DELETE FROM Member_Contact")
    fun DeleteMemberContact()

    @Query("SELECT * FROM Member_Contact WHERE sMemberID = :fsMemberID")
    fun GetMemberContact(fsMemberID : String): LiveData<List<EMemberContactInfo>>
}