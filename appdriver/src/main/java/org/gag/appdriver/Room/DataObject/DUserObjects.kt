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

    @Query("SELECT " +
                        "e.*  " +
                    "FROM " +
                        "Member_Info a " +
                    "JOIN " +
                        "User_Info b " +
                    "ON " +
                        "a.sGLPIDNoX = b.sGLPIDNoX " +
                    "JOIN " +
                        "Officer_Info c " +
                    "ON " +
                        "a.sMemberID = c.sMemberID " +
                    "JOIN  " +
                        "Object_Permissions d  " +
                    "ON " +
                        "d.owner_id = c.sPositnCd " +
                    "OR " +
                        "d.owner_id = c.sMemberID " +
                    "JOIN " +
                        " User_Objects e " +
                    "ON " +
                        "e.idxx = d.object_id " +
                    "JOIN " +
                        "Lodge_Calendar f " +
                    "ON " +
                        "c.sYearIDxx = f.sYearIDxx " +
                    "AND " +
                        "f.nYearxxxx = :fnYear " +
                    "WHERE " +
                        "b.sUserIDxx = :fsUserIDxx " +
                    "AND " +
                        "d.cRecdStat= '1' " +
                    "AND " +
                        "e.cRecdStat= '1'")
    fun GetUserObjects(fsUserIDxx : String, fnYear : String) : LiveData<List<EUserObjects>>

    @Query("SELECT " +
                        "e.*  " +
                    "FROM " +
                        "Member_Info a " +
                    "JOIN " +
                        "User_Info b " +
                    "ON " +
                        "a.sGLPIDNoX = b.sGLPIDNoX " +
                    "JOIN " +
                        "Officer_Info c " +
                    "ON " +
                        "a.sMemberID = c.sMemberID " +
                    "join  " +
                        "Object_Permissions d  " +
                    "ON " +
                        "d.owner_id = c.sPositnCd " +
                    "OR " +
                        "d.owner_id = c.sMemberID " +
                    "JOIN " +
                        " User_Objects e " +
                    "ON " +
                        "e.idxx = d.object_id " +
                    "JOIN " +
                        "Lodge_Calendar f " +
                    "ON " +
                        "c.sYearIDxx = f.sYearIDxx " +
                    "AND " +
                        "f.nYearxxxx = :fnYear " +
                    "WHERE " +
                        "b.sUserIDxx = :fsUserIDxx " +
                    "AND " +
                        "d.object_id= :fsIDxx " +
                    "AND " +
                        "d.cRecdStat= '1' " +
                    "AND " +
                        "e.cRecdStat= '1' " +
                    "LIMIT 1")
    fun GetObjectPermission(fsUserIDxx : String, fsIDxx : String, fnYear : String) : EUserObjects

    @Query("DELETE FROM User_Objects")
    fun Clear()
}