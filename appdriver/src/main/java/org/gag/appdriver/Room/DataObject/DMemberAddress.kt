package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.DataObject.DTownInfo.TownProvince
import org.gag.appdriver.Room.Entities.EMemberAddress

@Dao
interface DMemberAddress {

    @Upsert(entity = EMemberAddress::class)
    fun SaveMemberAddress(poMemberAddress: EMemberAddress)

    @Query("SELECT c.sAddrsIDx AS psAddrsIDx, " +
            "a.sTownIDxx AS psTownIDxx, " +
            "b.sProvIDxx AS psProvIDxx, " +
            "(a.sTownName || ', ' || b.sDescript) AS psTownProvNme, " +
            "c.sAddressx AS psAddressx, " +
            "c.cIsHomeAd AS isHomeAddr, " +
            "c.cRecdStat AS isActive " +
            "FROM Town_City a, Province b, Member_Address c " +
            "WHERE a.sProvIDxx = b.sProvIDxx " +
            "AND a.sTownIDxx = c.sTownIDxx " +
            "AND c.sMemberID = :fsMemberID")
    fun GetMemberAddress(fsMemberID : String): List<TownProvince>
}