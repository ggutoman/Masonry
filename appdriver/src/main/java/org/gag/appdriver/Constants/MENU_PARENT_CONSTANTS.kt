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
        R.drawable.person_2_baseline_member,
        "Members",
        "MEM",
        1,
        0
    ),

    MENU_ACCOUNTS(
        R.drawable.rounded_account_circle_24,
        "Accounts",
        "ACC",
        1,
        0
    )
}