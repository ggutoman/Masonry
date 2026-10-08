package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.OfficerInfo
import org.gag.appdriver.Room.Entities.EOfficer

@Dao
interface DOfficer {

    @Upsert(EOfficer::class)
    fun SaveOfficer(eOfficer: EOfficer)

    @Query("DELETE FROM Officer_Info")
    fun DeleteOfficers()

    @Query("SELECT " +
            "b.nYearxxxx, " +
            "a.sMemberID, a.sYearIDxx, a.sPositnCd," +
            "(c.sFrstName || ' ' || c.sLastName) sMemberNme," +
            "d.sPositnDs sPositionNme " +
            "FROM " +
            "Officer_Info a " +
            "JOIN Lodge_Calendar b ON (a.sYearIDxx = b.sYearIDxx) " +
            "JOIN  Member_Info c ON (a.sMemberID = c.sMemberID) " +
            "JOIN  Position_Info d ON (a.sPositnCd = d.sPositnCd) " +
            "WHERE (a.sMemberID != :fsMemberIDx) " +
            "AND b.nYearxxxx = :fsYear " +
            "AND d.sPositnCd " +
            "NOT IN " +
            "('P016', 'P000')" +
            "GROUP BY " +
            "a.sMemberID " +
            "ORDER BY " +
            "a.dModified DESC")
    fun ObserveOfficerList(fsMemberIDx : String, fsYear : String) : LiveData<List<OfficerInfo>>

    @Query("SELECT * FROM Officer_Info WHERE sMemberID= :fsMemberIDx AND sYearIDxx= :fsYearIDx")
    fun ObserveOfficeInfo(fsMemberIDx : String, fsYearIDx : String) : LiveData<EOfficer>

    @Query("""
            SELECT
                b.nYearxxxx,
                a.sMemberID, a.sYearIDxx, a.sPositnCd,
                (c.sFrstName || ' ' || c.sLastName) sMemberNme,
                d.sPositnDs sPositionNme
            FROM
                Officer_Info a
            JOIN 
                Lodge_Calendar b 
            ON 
                (a.sYearIDxx = b.sYearIDxx)
            JOIN  
                Member_Info c
            ON 
                (a.sMemberID = c.sMemberID)
            JOIN  
                Position_Info d 
            ON 
                (a.sPositnCd = d.sPositnCd)
            JOIN
                User_Info e
            ON
                c.sGLPIDNoX = e.sGLPIDNoX
            WHERE 
                e.sUserIDxx = :fsUserIDxx 
            AND
                b.nYearxxxx = :fnYearxx
            ORDER BY
                b.dThruDate
            DESC LIMIT 1
        """)
    fun ObserveCurrentRole(fsUserIDxx: String, fnYearxx : String): LiveData<OfficerInfo>

}