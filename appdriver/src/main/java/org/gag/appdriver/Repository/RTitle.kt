package org.gag.appdriver.Repository

import android.app.Application
import androidx.lifecycle.LiveData
import org.gag.appdriver.Room.Dao.DTitle
import org.gag.appdriver.Room.Entities.ETitle
import org.gag.appdriver.Room.ML_DBF

class RTitle(application: Application) {

    private val poDao: DTitle

    init {

        val db = ML_DBF.getDatabase(application)

        poDao = db.GetTitleDao()
    }

    fun GetTitles(): LiveData<List<ETitle>> {
        return poDao.GetTitles()
    }
}