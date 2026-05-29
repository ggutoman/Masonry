package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ETownCity

@Dao
interface DTownInfo {

    @Upsert(entity = ETownCity::class)
    fun SaveTownInfo(poTown: ETownCity)

    @Query("SELECT a.sTownIDxx AS psTownIDxx, b.sProvIDxx AS psProvIDxx, (a.sTownName || ',' || b.sDescript) AS psTownProvNme FROM Town_City a, Province b " +
            "WHERE (a.sProvIDxx = b.sProvIDxx) " +
            "AND (a.sTownName || ',' || b.sDescript) LIKE :fsTownProv")
    fun ObserveTownList(fsTownProv : String): LiveData<List<TownProvince>>

    data class TownProvince(
        val psTownIDxx : String,
        val psProvIDxx : String,
        val psTownProvNme : String
    )

}