package org.gag.appdriver.Room.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "Member_Master")
@Serializable
data class EMemberMaster(

    @PrimaryKey
    val sMemberID: String,

    val sLodgeIDx: String,
    val sGLPIDNoX: String,

    val sLastName: String,
    val sFrstName: String,
    val sSuffixNm: String,
    val sMiddName: String,

    val cCivilStat: String,
    val dBirthDte: String,

    val cMmbrStat: String,
    val dMembrshp: String?,
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

    val cRecdStat: String,

    val sModified: String?,
    val dModified: String?
)