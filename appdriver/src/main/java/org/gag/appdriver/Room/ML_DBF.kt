package org.gag.appdriver.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.gag.appdriver.Room.DataObject.DFundTurnover
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DMemberAddress
import org.gag.appdriver.Room.DataObject.DMemberContact
import org.gag.appdriver.Room.DataObject.DMemberEmailInfo
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DOfficer
import org.gag.appdriver.Room.DataObject.DOfficerHistory
import org.gag.appdriver.Room.DataObject.DPositionInfo
import org.gag.appdriver.Room.DataObject.DProvinceInfo
import org.gag.appdriver.Room.DataObject.DTitleInfo
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.DataObject.EProvinceInfo
import org.gag.appdriver.Room.Entities.EFundTurnOver
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EOfficerHistory
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.ETitle
import org.gag.appdriver.Room.Entities.ETownCity
import org.gag.appdriver.Room.Entities.EUserInfo

@Database(
    entities = [
        EUserInfo::class,
        EMemberInfo::class,
        ELodgeInfo::class,
        EPosition::class,
        ETitle::class,
        ETownCity::class,
        EProvinceInfo::class,
        EMemberAddress::class,
        EMemberContactInfo::class,
        EMemberEmailInfo::class,
        ELodgeCalendar::class,
        EOfficer::class,
        EOfficerHistory::class,
        EFundTurnOver::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ML_DBF: RoomDatabase() {

    abstract fun GetUserDao(): DUserInfo
    abstract fun GetMemberDao(): DMemberInfo
    abstract fun GetLodge(): DLodgeInfo
    abstract fun GetPosition(): DPositionInfo
    abstract fun GetTitle() : DTitleInfo
    abstract fun GetTownCity() : DTownInfo
    abstract fun GetProvince() : DProvinceInfo
    abstract fun GetMemberAddress() : DMemberAddress
    abstract fun GetMemberContact() : DMemberContact
    abstract fun GetMemberEmail() : DMemberEmailInfo
    abstract fun GetLodgeCalendar() : DLodgeCalendar
    abstract fun GetOfficer() : DOfficer
    abstract fun GetOfficerHistory() : DOfficerHistory
    abstract fun GetFundTurnOver() : DFundTurnover

    companion object {

        @Volatile
        private var instance: ML_DBF? = null

        fun getDatabase(context: Context): ML_DBF {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ML_DBF::class.java,
                    "Masonry_DB"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}