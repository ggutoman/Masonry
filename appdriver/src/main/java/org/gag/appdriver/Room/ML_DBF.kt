package org.gag.appdriver.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.gag.appdriver.Room.DataObject.DAnnualDetail
import org.gag.appdriver.Room.DataObject.DAnnualMaster
import org.gag.appdriver.Room.DataObject.DDisbursement
import org.gag.appdriver.Room.DataObject.DFundLedger
import org.gag.appdriver.Room.DataObject.DFundMaster
import org.gag.appdriver.Room.DataObject.DFundTurnover
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.DataObject.DLodgeInfo
import org.gag.appdriver.Room.DataObject.DMemberAddress
import org.gag.appdriver.Room.DataObject.DMemberContact
import org.gag.appdriver.Room.DataObject.DMemberEmailInfo
import org.gag.appdriver.Room.DataObject.DMemberInfo
import org.gag.appdriver.Room.DataObject.DObjectPermission
import org.gag.appdriver.Room.DataObject.DOfficer
import org.gag.appdriver.Room.DataObject.DOfficerHistory
import org.gag.appdriver.Room.DataObject.DPositionInfo
import org.gag.appdriver.Room.DataObject.DProjects
import org.gag.appdriver.Room.DataObject.DProvinceInfo
import org.gag.appdriver.Room.DataObject.DReceipt
import org.gag.appdriver.Room.DataObject.DSysNotif
import org.gag.appdriver.Room.DataObject.DSysReports
import org.gag.appdriver.Room.DataObject.DTownInfo
import org.gag.appdriver.Room.DataObject.DUserInfo
import org.gag.appdriver.Room.DataObject.DUserObjects
import org.gag.appdriver.Room.DataObject.EProvinceInfo
import org.gag.appdriver.Room.Entities.EAnnualDetail
import org.gag.appdriver.Room.Entities.EAnnualMaster
import org.gag.appdriver.Room.Entities.EDisbursement
import org.gag.appdriver.Room.Entities.EDisbursementAttachment
import org.gag.appdriver.Room.Entities.EFundLedger
import org.gag.appdriver.Room.Entities.EFundMaster
import org.gag.appdriver.Room.Entities.EFundTurnOver
import org.gag.appdriver.Room.Entities.ELodgeCalendar
import org.gag.appdriver.Room.Entities.ELodgeInfo
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContactInfo
import org.gag.appdriver.Room.Entities.EMemberEmailInfo
import org.gag.appdriver.Room.Entities.EMemberInfo
import org.gag.appdriver.Room.Entities.EObjectPermissions
import org.gag.appdriver.Room.Entities.EOfficer
import org.gag.appdriver.Room.Entities.EOfficerHistory
import org.gag.appdriver.Room.Entities.EPosition
import org.gag.appdriver.Room.Entities.EProjectDetail
import org.gag.appdriver.Room.Entities.EProjectMaster
import org.gag.appdriver.Room.Entities.EReceiptDetail
import org.gag.appdriver.Room.Entities.EReceiptMaster
import org.gag.appdriver.Room.Entities.EReports
import org.gag.appdriver.Room.Entities.ESysNotifs
import org.gag.appdriver.Room.Entities.ETownCity
import org.gag.appdriver.Room.Entities.EUserInfo
import org.gag.appdriver.Room.Entities.EUserObjects

@Database(
    entities = [
        EUserInfo::class,
        EMemberInfo::class,
        ELodgeInfo::class,
        EPosition::class,
        ETownCity::class,
        EProvinceInfo::class,
        EMemberAddress::class,
        EMemberContactInfo::class,
        EMemberEmailInfo::class,
        ELodgeCalendar::class,
        EOfficer::class,
        EFundMaster::class,
        EFundLedger::class,
        EOfficerHistory::class,
        EFundTurnOver::class,
        EAnnualMaster::class,
        EAnnualDetail::class,
        EProjectMaster::class,
        EProjectDetail::class,
        EReceiptMaster::class,
        EReceiptDetail::class,
        EDisbursement::class,
        EDisbursementAttachment::class,
        EUserObjects::class,
        EReports::class,
        ESysNotifs::class,
        EObjectPermissions::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ML_DBF: RoomDatabase() {

    abstract fun GetUserDao(): DUserInfo
    abstract fun GetMemberDao(): DMemberInfo
    abstract fun GetLodge(): DLodgeInfo
    abstract fun GetPosition(): DPositionInfo
    abstract fun GetTownCity() : DTownInfo
    abstract fun GetProvince() : DProvinceInfo
    abstract fun GetMemberAddress() : DMemberAddress
    abstract fun GetMemberContact() : DMemberContact
    abstract fun GetMemberEmail() : DMemberEmailInfo
    abstract fun GetLodgeCalendar() : DLodgeCalendar
    abstract fun GetOfficer() : DOfficer
    abstract fun GetOfficerHistory() : DOfficerHistory
    abstract fun GetFundMaster() : DFundMaster
    abstract fun GetFundLedger() : DFundLedger
    abstract fun GetFundTurnOver() : DFundTurnover
    abstract fun GetAnnualMaster() : DAnnualMaster
    abstract fun GetAnnualDetail() : DAnnualDetail
    abstract fun GetProject() : DProjects
    abstract fun GetReceipt() : DReceipt
    abstract fun GetDisbursement() : DDisbursement
    abstract fun GetUserObjects() : DUserObjects
    abstract fun GetReportObjects() : DSysReports
    abstract fun GetNotifs() : DSysNotif
    abstract fun GetPermissions() : DObjectPermission

    companion object {

        @Volatile
        private var instance: ML_DBF? = null

        /** ROOM DB INITIALIZATION **/
        fun getDatabase(context: Context): ML_DBF {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ML_DBF::class.java,
                    "Masonry_DB"
                )
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build()
                instance = newInstance
                newInstance
            }
        }

        /** MIGRATION SETUP **/

        private fun SupportSQLiteDatabase.columnExists(table: String, column: String): Boolean {
            query("PRAGMA table_info(`$table`)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex).equals(column, ignoreCase = true)) return true
                }
            }
            return false
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                if (!db.columnExists("Member_Info", "sModified")) {
                    db.execSQL("ALTER TABLE `Member_Info` ADD COLUMN `sModified` TEXT")
                }

                if (!db.columnExists("Member_Info", "dModified")) {
                    db.execSQL("ALTER TABLE `Member_Info` ADD COLUMN `dModified` TEXT")
                }

            }
        }
    }
}