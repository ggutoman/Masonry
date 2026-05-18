package org.gag.appdriver.Libraries.DateUtil

import android.annotation.SuppressLint
import android.os.Build
import org.gag.appdriver.Constants.DATE_CONSTANTS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class DateRepository {

    @SuppressLint("SimpleDateFormat")
    fun GetCurrentDate() : String{
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
            SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript).format(Date())
        else
            LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_CONSTANTS.DATETIME_FORMAT.fsDescript))
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
}