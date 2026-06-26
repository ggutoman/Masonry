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

    MENU_LODGE(
        R.drawable.baseline_lodge_icon,
        "Lodge",
        "LDGE",
        1,
        2
    ),

    MENU_MEMBER(
        R.drawable.baseline_member,
        "Members",
        "MEM",
        1,
        2
    ),

    MENU_FUNDS(
        R.drawable.baseline_receipts,
        "Funds",
        "FND",
        1,
        1
    ),

    MENU_ACCOUNTS(
        R.drawable.baseline_settings,
        "Settings",
        "ACC",
        1,
        0
    )
}