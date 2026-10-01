package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.AnnualMembers
import org.gag.appdriver.App.Models.AnnualSummary
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EObjectPermissions

@Dao
interface DObjectPermission {

    @Upsert
    fun SavePermission(ePermission: EObjectPermissions)

    @Query("DELETE FROM Object_Permissions")
    fun Clear()
}