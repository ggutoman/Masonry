package org.gag.appdriver.Constants

enum class MENU_ITEM_CONSTANTS(val fsTitlex : String, val fsIDxx : String, val fsParentIDx : String, val fnActive : Int, val fnLevel : Int){


    MENU_ITEM_DOWNLOAD_PARAMETERS(
        "Download Data",
        "ACC001",
        "ACC",
        1,
        2
    ),

    MENU_ITEM_ACCOUNT_DETAILS(
        "Account Info",
        "ACC002",
        "ACC",
        1,
        1
    ),

    MENU_ITEM_LOD_ACCOUNT(
        "Logout",
        "ACC003",
        "ACC",
        1,
        1
    ),

    MENU_ITEM_TURNOVER_FUNDS(
        "Turnover Funds",
        "DISB001",
        "DISB",
        1,
        1
    ),

    MENU_ITEM_CREATE_LODGE(
        "Create Lodge Calendar",
        "MEM001",
        "MEM",
        1,
        2
    ),

    MENU_ITEM_CREATE_MEMBER(
    "Add Member",
    "MEM002",
    "MEM",
        1,
        2
    ),

    MENU_ITEM_ASSIGN_OFFICER(
    "Assign Officer",
    "MEM003",
    "MEM",
    1,
    2
    )
}