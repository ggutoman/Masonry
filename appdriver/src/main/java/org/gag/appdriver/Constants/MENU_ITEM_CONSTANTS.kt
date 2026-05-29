package org.gag.appdriver.Constants

enum class MENU_ITEM_CONSTANTS(val fsTitlex : String, val fsIDxx : String, val fsParentIDx : String, val fnActive : Int, val fnLevel : Int){

    MENU_ITEM_UPDATE_ACCOUNT(
        "Update",
        "ACC001",
        "ACC",
        1,
        0
    ),

    MENU_ITEM_CREATE_MEMBER(
    "Create Member",
    "ACC002",
    "ACC",
        1,
        1
    )
}