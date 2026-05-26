package org.gag.appdriver.Constants

enum class ENCRYPT_CONSTANTS(val fsAlgo : String, val fsMethod : String, val fsKey : String, val fsSrc : String, val fsHash : String){

    AES_METHOD_ZERO_PADDING(
        "AES",
        "AES/CTR/NoPadding",
        "MSNLDG2026",
        "260531",
        "SHA-256"
    )
}