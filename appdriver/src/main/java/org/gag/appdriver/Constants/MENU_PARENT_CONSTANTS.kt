package org.gag.appdriver.Constants

import org.gag.appdriver.R

enum class MENU_PARENT_CONSTANTS(val fnIconx : Int, val fsTitlex : String, val fsIDxx : String, val fnActive : Int, val fnLevel : Int){

    MENU_HOME(
        R.drawable.ic_baseline_home,
        "Home",
        "HME",
        1,
        0
    ),

    MENU_MEMBER(
        R.drawable.baseline_member,
        "Members",
        "MEM",
        1,
        2
    ),

    MENU_DISBURSEMENT(
        R.drawable.baseline_receipts,
        "Disbursement",
        "DISB",
        1,
        1
    ),

    MENU_ACCOUNTS(
        R.drawable.baseline_settings,
        "Settings",
        "ACC",
        1,
        1
    )
}