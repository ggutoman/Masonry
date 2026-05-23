package org.gag.appdriver.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.Entities.EUserInfo

@Database(
    entities = [
        EUserInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ML_DBF: RoomDatabase() {

    abstract fun GetUserDao(): DUserInfo

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