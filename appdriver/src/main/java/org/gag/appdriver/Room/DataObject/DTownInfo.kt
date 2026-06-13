package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.App.Models.TownProvince
import org.gag.appdriver.Room.Entities.ETownCity

@Dao
interface DTownInfo {

    @Upsert(entity = ETownCity::class)
    fun SaveTownInfo(poTown: ETownCity)

    @Query("SELECT " +
            "'' AS psAddrsIDx, " +
            "a.sTownIDxx AS psTownIDxx, " +
            "b.sProvIDxx AS psProvIDxx, " +
            "(a.sTownName || ', ' || b.sDescript) AS psTownProvNme, " +
            "'' AS psAddressx, " +
            "'0' AS isHomeAddr, " +
            "'0' AS isActive " +
            "FROM Town_City a, Province b " +
            "WHERE a.sProvIDxx = b.sProvIDxx " +
            "AND (a.sTownName || ',' || b.sDescript) LIKE :fsSearch ")
    fun SearchTown(fsSearch : String): LiveData<List<TownProvince>>

}