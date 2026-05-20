package org.gag.appdriver

import androidx.test.platform.app.InstrumentationRegistry
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.ML_DBF
import org.junit.Before
import org.junit.Test

class TestUserEntity {

    lateinit var dao : DUserInfo
    lateinit var entity : EUserInfo

    @Before
    fun setup(){

        dao = ML_DBF.getDatabase(
            InstrumentationRegistry
                .getInstrumentation()
                .context
        ) as DUserInfo

        entity = EUserInfo(
            "MLUSR2026050001",
            "001",
            "Gutoman, Guillier A",
            "ID20260501001",
            "Gutoman",
            "1998-02-01",
            1,
            "0",
            "MLUSR2026050001",
            "2026-05-01"

        )
    }

    @Test
    fun SaveUser(){

        dao.SaveUserInfo(entity)
    }
}