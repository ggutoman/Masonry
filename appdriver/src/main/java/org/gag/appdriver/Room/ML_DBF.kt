package org.gag.appdriver.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.gag.appdriver.Room.Entities.EUserInfo

@Database(
    entities = [
        EUserInfo::class
    ],
    version = 6,
    exportSchema = false
)
abstract class ML_DBF: RoomDatabase() {

    abstract fun GetUserDao(): EUserInfo

    companion object {

        var instance: ML_DBF? = null

        fun getDatabase(context: Context): ML_DBF? {

            if (instance == null) {

                synchronized(ML_DBF::class) {

                    instance = Room.databaseBuilder(
                        context,
                        ML_DBF::class.java,
                        "Masonry_DB"
                    )
                        .fallbackToDestructiveMigration(false)
                        .allowMainThreadQueries()
                        .build()

                }

            }

            return instance
        }

    }
}