package org.gag.appdriver

import org.gag.appdriver.Libraries.DateUtil.DateRepository
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