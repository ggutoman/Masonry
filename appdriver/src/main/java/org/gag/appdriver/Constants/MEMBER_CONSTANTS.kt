package org.gag.appdriver.Constants

enum class MEMBER_CONSTANTS(val fsIDx : String, val fsDescr : String){

    /**PERSONAL STATUS**/
    STATUS_SINGLE("0", "Single"),
    STATUS_MARRIED("1", "Married"),
    STATUS_WIDOWED("2", "Widowed"),
    STATUS_SEPARATED("3", "Separated"),

    /**OFFICER LEVEL**/
    STATUS_ELECTED("0", "Elected"),
    STATUS_APPOINTED("1", "Appointed"),

    /**OFFICER STATUS**/
    STATUS_OFFICER_SUSPENDED("0", "Suspended"),
    STATUS_OFFICER_ACTIVE("1", "Active/Incumbent"),
    STATUS_OFFICER_REASSIGN("2", "Reassigned"),
    STATUS_OFFICER_REMOVED("3", "Removed"),
    STATUS_OFFICER_RESIGNED("4", "Resigned"),
    STATUS_OFFICER_DECEASE("5", "Deceased"),

    /**MEMBER STATUS**/
    STATUS_REGULAR_MEMBER("0", "Regular"),
    STATUS_LIFE_MEMBER("1", "Life"),
    STATUS_DUAL_MEMBER("2", "Dual"),
    STATUS_DECEASED_MEMBER("3", "Deceased")
}