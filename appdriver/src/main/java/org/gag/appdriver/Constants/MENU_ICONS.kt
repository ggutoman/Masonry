package org.gag.appdriver.Constants

import org.gag.appdriver.R

enum class MENU_ICONS(val fsIDxx : String, val fnIconxx : Int){


    MENU_HOME(
        "000",
        R.drawable.ic_baseline_home
    ),

    MENU_MEMBER(
        "001",
        R.drawable.baseline_member
    ),

    MENU_PROJECTS(
        "002",
        R.drawable.baseline_project
    ),

    MENU_ACCOUNTING(
        "003",
        R.drawable.baseline_funds
    ),

    MENU_SETTINGS(
        "004",
        R.drawable.baseline_settings
    );

    companion object {
        fun fromId(id: String): MENU_ICONS? {
            return MENU_ICONS.entries.find { it.fsIDxx == id }
        }
    }
}