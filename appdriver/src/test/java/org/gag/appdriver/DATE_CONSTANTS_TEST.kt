package org.gag.appdriver

import org.gag.appdriver.Constants.DATE_CONSTANTS
import org.junit.Test

class DATE_CONSTANTS_TEST {

    @Test
    fun TestByName(){
        println(DATE_CONSTANTS.DATE_FORMAT.name)
    }

    @Test
    fun TestByDescript(){
        println(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
    }

}