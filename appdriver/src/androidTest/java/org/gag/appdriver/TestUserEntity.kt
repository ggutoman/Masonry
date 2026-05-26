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
        ).GetUserDao() as DUserInfo

        entity = EUserInfo(
            "MLUSR2026050001",
            "Gutoman, Guillier A",
            "A665A45920422F9D417E4867EFDC4FB8A04A1F3FFF1FA07E998E86F7F7A27AE3",
            "001",
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