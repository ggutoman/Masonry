package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Member_Info", primaryKeys = ["sMemberID"])
@Serializable
data class EMemberInfo(
    var sMemberID: String,
    var sLodgeIDx: String?,
    var sGLPIDNoX: String?,
    var dInitiatn: String?,
    var dPassedxx: String?,
    var dRaisingX: String?,
    var sLastName: String?,
    var sFrstName: String?,
    var sMiddName: String?,
    var sSuffixNm: String?,
    var cCvilStat: String?,
    var dBirthDte: String?,
    var cMmbrStat: String?,
    var dMembrshp: String?,
    var dSuspendx: String?,
    var sPositnCd: String?,
    var cRecdStat: String?,
    var sModified: String?,
    var dModified: String?
)