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
        0
    ),

    MENU_ITEM_LOD_ACCOUNT(
        "Logout",
        "ACC003",
        "ACC",
        1,
        0
    ),

    /**LODGE INFO**/
    MENU_ITEM_CREATE_LODGE(
        "Create Lodge",
        "LDGE0001",
        "LDGE",
        1,
        4
    ),

    MENU_ITEM_CREATE_LODGE_YEAR(
        "Create Lodge Calendar",
        "LDGE0002",
        "LDGE",
        1,
        2
    ),

    MENU_ITEM_VIEW_LODGE(
        "View Lodges",
        "LDGE0003",
        "LDGE",
        1,
        4
    ),

    MENU_ITEM_LODGE_CALENDARS(
        "View Lodge Calendars",
        "LDGE0004",
        "LDGE",
        1,
        2
    ),

    /**MEMBER INFO**/
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
    ),

    MENU_ITEM_OFFICER_HISTORY(
        "Officer History",
        "MEM004",
        "MEM",
        1,
        2
    ),

    MENU_ITEM_TURNOVER_FUNDS(
        "Turnover Funds",
        "FND001",
        "FND",
        1,
        1
    ),

    MENU_ITEM_FUNDS_HISTORY(
        "Fund History",
        "FND002",
        "FND",
        1,
        1
    ),

    MENU_ITEM_FUND_SUMMARY(
        "Fund Summary",
        "FND003",
        "FND",
        1,
        1
    ),

    MENU_ITEM_CREATE_ANNUAL(
        "Create Annual Billing",
        "ANNL001",
        "ANNL",
        1,
        1
    ),

    MENU_ITEM_LODGE_ANNUAL_HISTORY(
        "Annual Billing History",
        "ANNL002",
        "ANNL",
        1,
        1
    ),

    MENU_ITEM_LODGE_ANNUAL_SUMMARY(
        "Annual Summary",
        "ANNL003",
        "ANNL",
        1,
        1
    ),

    MENU_ITEM_CREATE_PROJECTS(
        "Create Project",
        "PROJ001",
        "PROJ",
        1,
        1
    ),

    MENU_ITEM_PROJECTS(
        "Project History",
        "PROJ002",
        "PROJ",
        1,
        1
    ),
}