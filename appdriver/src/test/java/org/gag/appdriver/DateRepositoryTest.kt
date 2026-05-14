package org.gag.appdriver

import org.gag.appdriver.Libraries.DateRepository
import org.junit.Test

class DateRepositoryTest {

    @Test
    fun TestCurrentDate(){
        println(DateRepository().GetCurrentDate())
    }

    @Test
    fun TestCurrentDateTime(){
        println(DateRepository().GetCurrentDateTime())
    }
}