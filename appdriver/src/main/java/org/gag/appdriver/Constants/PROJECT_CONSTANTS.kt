package org.gag.appdriver.Constants

enum class PROJECT_CONSTANTS(val fsIDx : String, val fsDescr : String){

    /**PROJECT TYPES**/
    EDUCATION("0", "Educational Program"),
    HEALTH("1", "Health Program"),
    OUTREACH("2", "Outreach Program"),
    ENVIRONMENTAL("3", "Environmental Program"),

    /**PROJECT STATUS**/
    PLANNING("0", "Planning"),
    ON_GOING("1", "On Going"),
    COMPLETED("2", "Completed"),
    CANCELLED("3", "Cancelled"),
}