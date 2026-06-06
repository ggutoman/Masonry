package org.gag.appdriver.Libraries.DateUtil

import android.annotation.SuppressLint
import android.os.Build
import org.gag.appdriver.Constants.DATE_CONSTANTS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class DateRepository {

    @SuppressLint("SimpleDateFormat")
    fun GetCurrentDate() : String{
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
            SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript).format(Date())
        else
            LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_CONSTANTS.DATE_FORMAT.fsDescript))
    }

    @SuppressLint("SimpleDateFormat")
    fun GetCurrentDateTime() : String{
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
            SimpleDateFormat(DATE_CONSTANTS.DATETIME_FORMAT.fsDescript).format(Date())
        else
            LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_CONSTANTS.DATETIME_FORMAT.fsDescript))
    }

    @SuppressLint("SimpleDateFormat")
    fun FormatToDate(fsParam : String) : String{
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)

            SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
                .format(fsParam)
        else
            LocalDate.parse(fsParam)
                .format(
                    DateTimeFormatter.ofPattern(
                        DATE_CONSTANTS.DATE_FORMAT.fsDescript
                )
            )
    }

    @SuppressLint("SimpleDateFormat")
    fun GetCountedDate(fnCount : Int, fnDateIndex : Int, fbIsAdd : Boolean) : String{

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){

            return Calendar.getInstance().let {

                val result = when (fnDateIndex){

                    0 -> {
                        if (fbIsAdd){
                            it.add(Calendar.MONTH, fnCount)
                            it
                        }else{
                            it.add(Calendar.MONTH, -kotlin.math.abs(fnCount))
                            it
                        }
                    }
                    1 -> {
                        if (fbIsAdd){
                            it.add(Calendar.DATE, fnCount)
                            it
                        }else{
                            it.add(Calendar.DATE, -kotlin.math.abs(fnCount))
                            it
                        }
                    }
                    2 -> {
                        if (fbIsAdd){
                            it.add(Calendar.YEAR, fnCount)
                            it
                        }else{
                            it.add(Calendar.YEAR, -kotlin.math.abs(fnCount))
                            it
                        }
                    }

                    else -> it
                }

                SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
                    .format(result.time)
            }

        }else{

            return LocalDate.now().let {

                val result = when (fnDateIndex){

                    0 -> {
                        if (fbIsAdd){
                            it.plusMonths(fnCount.toLong())
                        }else{
                            it.minusMonths(fnCount.toLong())
                        }
                    }
                    1 -> {
                        if (fbIsAdd){
                            it.plusDays(fnCount.toLong())
                        }else{
                            it.minusMonths(fnCount.toLong())
                        }
                    }
                    2 -> {
                        if (fbIsAdd){
                            it.plusYears(fnCount.toLong())
                        }else{
                            it.plusYears(fnCount.toLong())
                        }
                    }

                    else -> it
                }

                result.format(
                        DateTimeFormatter.ofPattern(
                            DATE_CONSTANTS.DATE_FORMAT.fsDescript)
                    )

            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun FormatLongDate(fsParam : Long) : String{
        return SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
            .format(fsParam)
    }
}