package org.gag.appdriver.Room.DataObject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.ETownCity

@Dao
interface DProvinceInfo {

    @Upsert(entity = EProvinceInfo::class)
    fun SaveProvince(poProvince: EProvinceInfo)

}