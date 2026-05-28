package org.gag.appdriver.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import org.gag.appdriver.Room.Dao.DLodge
import org.gag.appdriver.Room.Entities.ELodge
import org.gag.appdriver.Room.ML_DBF

class RLodge(application: Application) {

    private val poDao: DLodge

    init {

        val db = ML_DBF.getDatabase(application)

        poDao = db.GetLodgeDao()
    }

    fun GetLodges(): LiveData<List<ELodge>> {
        return poDao.GetLodges()
    }
}