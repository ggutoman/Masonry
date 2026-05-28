package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Member_Info", primaryKeys = ["sMemberID"])
@Serializable
data class EMemberInfo(
    var sMemberID: String,
    var sLodgeIDx: String,
    var sGLPIDNoX: String,
    var sMemberNm: String,
    var cCvilStat: String,
    var dBirthDte: String,
    var cMmbrStat: String,
    var dMembrshp: String,
    var dSuspendx: String?,
    var sTitleIDx: String?,
    var dPetition: String?,
    var dInitiatn: String?,
    var dPassedxx: String?,
    var dRaisingX: String?,
    var sSponsor1: String?,
    var sSponsor2: String?,
    var sSponsor3: String?,
    var nDueBalxx: Double,
    var nPrjBalxx: Double,
    var sPositnCd: String?,
    var cRecdStat: String
)