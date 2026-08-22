package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EReports

@Dao
interface DSysReports {

    @Upsert
    fun SaveReport(eReports : EReports)
}