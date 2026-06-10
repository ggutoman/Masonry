package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EOfficer

@Dao
interface DOfficer {

    @Upsert(EOfficer::class)
    fun SaveOfficer(eOfficer: EOfficer)

    @Query("DELETE FROM Officer_Info")
    fun DeleteOfficers()

    @Query("SELECT " +
            "b.nYearxxxx, " +
            "a.sMemberID, a.sYearIDxx, a.cAppointx, a.cStatusxx, a.sPositnCd," +
            "(c.sFrstName || ' ' || c.sLastName) sMemberNme," +
            "d.sPositnDs sPositionNme " +
            "FROM " +
            "Officer_Info a " +
            "JOIN Lodge_Calendar b ON (a.sYearIDxx = b.sYearIDxx) " +
            "JOIN  Member_Info c ON (a.sMemberID = c.sMemberID) " +
            "JOIN  Position_Info d ON (a.sPositnCd = d.sPositnCd) " +
            "WHERE (a.sMemberID != :fsMemberIDx) " +
            "AND (b.dFromDate >= :fsDateFrom " +
            "AND b.dThruDate <= :fsDateTo) " +
            "ORDER BY " +
            "a.dModified DESC, " +
            "a.nEntryNox ASC ")
    fun ObserveOfficerList(fsMemberIDx : String, fsDateFrom : String, fsDateTo : String) : LiveData<List<OfficerList>>

    @Query("SELECT * FROM Officer_Info WHERE sMemberID= :fsMemberIDx AND sYearIDxx= :fsYearIDx")
    fun ObserveOfficeInfo(fsMemberIDx : String, fsYearIDx : String) : LiveData<EOfficer>

    data class OfficerList(

        val nYearxxxx : String,
        val sMemberID : String,
        val sYearIDxx : String,
        val cAppointx : String,
        val cStatusxx : String,
        val sPositnCd : String,
        val sMemberNme : String,
        val sPositionNme : String,
    )
}