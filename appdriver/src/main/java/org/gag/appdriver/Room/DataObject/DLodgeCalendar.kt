package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.LodgeCalendarList
import org.gag.appdriver.Room.Entities.ELodgeCalendar

@Dao
interface DLodgeCalendar {

    @Upsert(ELodgeCalendar::class)
    fun SaveLodgeCalendar(lodgeCalendar : ELodgeCalendar)

    @Query("DELETE FROM Lodge_Calendar")
    fun DeleteLodgeCalendar()

    @Query("SELECT * FROM Lodge_Calendar WHERE sYearIDxx= :sYearIDx")
    fun ObserveLodgeCalendarInfo(sYearIDx : String) : LiveData<ELodgeCalendar>

    @Query("SELECT a.sYearIDxx sYearIDxx, a.sLodgeIDx sLodgeIDx, a.nYearxxxx nYearxxxx," +
            "a.dFromDate dFromDate, a.dThruDate dThruDate, b.sLodgeNme sLodgeNme " +
            "FROM Lodge_Calendar a, Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx) " +
            "WHERE a.sLodgeIDx = :fsLodgeIDxx")
    fun GetLodgeCalendarList(fsLodgeIDxx : String): LiveData<List<LodgeCalendarList>>

    @Query("SELECT a.sYearIDxx sYearIDxx, a.sLodgeIDx sLodgeIDx, a.nYearxxxx nYearxxxx," +
            "a.dFromDate dFromDate, a.dThruDate dThruDate, b.sLodgeNme sLodgeNme " +
            "FROM Lodge_Calendar a, Lodge_Info b ON (a.sLodgeIDx = b.sLodgeIDx) " +
            "WHERE a.sLodgeIDx = :fsLodgeIDxx " +
            "AND a.dFromDate >= :fsDateFrom " +
            "AND a.dThruDate <= :fsDateTo")
    fun GetLodgeCalendarList(fsLodgeIDxx : String, fsDateFrom : String, fsDateTo : String): LiveData<List<LodgeCalendarList>>

}