package org.gag.appdriver.App.Models

import kotlinx.serialization.Serializable
import org.gag.appdriver.Room.Entities.EFundLedger
import org.gag.appdriver.Room.Entities.EFundMaster
import org.gag.appdriver.Room.Entities.EFundTurnOver

@Serializable
data class LodgeFundInfo(
    val master: EFundMaster,
    val ledger: List<EFundLedger>,
    val turnover: List<EFundTurnOver>
)