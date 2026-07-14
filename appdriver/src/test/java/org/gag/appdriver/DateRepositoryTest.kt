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

    @Test
    fun TestFormatDateTime(){
        println(DateRepository().FormatDateTime("2026-07-14 10:00:00", "yyyy-MM-dd HH:mm:ss"))
    }
}