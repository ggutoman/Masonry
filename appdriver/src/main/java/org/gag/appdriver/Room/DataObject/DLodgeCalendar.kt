package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.ELodgeInfo

@Dao
interface DLodgeCalendar {

    @Upsert(ELodgeCalendar::class)
    fun SaveLodgeCalendar(lodgeCalendar : ELodgeCalendar)

    @Query("SELECT a.sYearIDxx sYearIDxx, a.sLodgeIDx sLodgeIDx, a.nYearxxxx nYearxxxx," +
            "a.dFromDate dFromDate, a.dThruDate dThruDate, b.sLodgeNme sLodgeNme " +
            "FROM Lodge_Calendar a, Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx)")
    fun GetLodgeCalendarList(): LiveData<List<LodgeCalendarList>>

    data class LodgeCalendarList(
        val sYearIDxx: String,
        val sLodgeIDx: String,
        val nYearxxxx: String,
        val dFromDate: String,
        val dThruDate: String,
        val sLodgeNme : String
    )
}