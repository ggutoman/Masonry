package org.gag.appdriver.Constants

enum class MEMBER_STATUS(val fsIDx : String, val fsDescr : String){

    /**PERSONAL STATUS**/
    URL_STATUS_SINGLE("0", "Single"),
    URL_STATUS_MARRIED("1", "Married"),
    URL_STATUS_WIDOWED("2", "Widowed"),
    URL_STATUS_SEPARATED("3", "Separated"),

    /**ACCOUNT STATUS**/
    URL_STATUS_ACTIVE("1", "Active"),
    URL_STATUS_INACTIVE("2", "Inactive"),
    URL_STATUS_SUSPENDED("3", "Suspended")
}