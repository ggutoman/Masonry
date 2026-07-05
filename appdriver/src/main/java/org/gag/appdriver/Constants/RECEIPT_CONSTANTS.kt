package org.gag.appdriver.Constants

enum class RECEIPT_CONSTANTS(val fsID : String, val fsDescript : String){
    RECEIPT_ANNUAL(
        "DUES",
        "Annual Dues"
    ),
    RECEIPT_PROJECT(
        "PROJ",
        "Project"
    ),
    RECEIPT_BANK(
        "BINT",
        "Bank Interest"
    ),
    RECEIPT_OTHERS(
        "OTHR",
        "Others"
    )
}