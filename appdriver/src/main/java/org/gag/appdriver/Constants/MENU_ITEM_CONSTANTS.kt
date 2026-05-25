package org.gag.appdriver.Constants

import org.gag.appdriver.R

enum class MENU_ITEM_CONSTANTS(val fsTitlex : String, val fsIDxx : String, val fsParentIDx : String, val fnActive : Int){

    MENU_ITEM_UPDATE_ACCOUNT(
        "Update",
        "ACC001",
        "ACC",
        1
    ),

    MENU_ITEM_ASSIGN_ROLE(
    "Assign Role",
    "ACC002",
    "ACC",
        0
    )
}