package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity(tableName = "Member_Info", primaryKeys = ["sMemberID"])
@Serializable
data class EMemberInfo(
    val sMemberID: String,
    val sLodgeIDx: String,
    val sGLPIDNoX: String,
    val sMemberNm: String,
    val cCvilStat: String,
    val dBirthDte: String,
    val cMmbrStat: String,
    val dMembrshp: String,
    val dSuspendx: String?,
    val sTitleIDx: String?,
    val dPetition: String?,
    val dInitiatn: String?,
    val dPassedxx: String?,
    val dRaisingX: String?,
    val sSponsor1: String?,
    val sSponsor2: String?,
    val sSponsor3: String?,
    val nDueBalxx: Double,
    val nPrjBalxx: Double,
    val sPositnCd: String?,
    val cRecdStat: String
)