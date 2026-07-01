package org.gag.appdriver.Libraries.DateUtil

import android.annotation.SuppressLint
import android.os.Build
import org.gag.appdriver.Constants.DATE_CONSTANTS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    fun IsDateCompared(fsDate1: String, fsDate2: String): Boolean {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {

            val sdf = SimpleDateFormat(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
            val date1 = sdf.parse(fsDate1)
            val date2 = sdf.parse(fsDate2)

            date1 != null && date2 != null && date1.before(date2)
        } else {

            val formatter = DateTimeFormatter.ofPattern(DATE_CONSTANTS.DATE_FORMAT.fsDescript)
            val date1 = LocalDate.parse(fsDate1, formatter)
            val date2 = LocalDate.parse(fsDate2, formatter)

            date1.isBefore(date2)
        }
    }

    /**
     * fnCount => count to calendar
     * fnDateIndex => index to count
     * fbIsAdd => is add or subtract
     * **/
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

    fun FormatDate(input: String, fsFormat: String): String {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = LocalDate.parse(input)
            val formatter = DateTimeFormatter.ofPattern(fsFormat, Locale.getDefault())
            date.format(formatter)
        } else {
            // Use java.text.SimpleDateFormat for older Android versions
            val parser = SimpleDateFormat(fsFormat, Locale.getDefault())
            val date = parser.parse(input)
            val formatter = SimpleDateFormat(fsFormat, Locale.getDefault())
            formatter.format(date!!)
        }
    }

    fun ConvertStringDate(input: String, fsFormat: String): Date? {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val formatter = DateTimeFormatter.ofPattern(fsFormat, Locale.getDefault())
            val localDate = LocalDate.parse(input, formatter)


            val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            Date.from(instant)
        } else {

            val parser = SimpleDateFormat(fsFormat, Locale.getDefault())
            parser.parse(input)
        }
    }

    fun ConvertDateString(date: Date, fsFormat: String): String {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val formatter = DateTimeFormatter.ofPattern(fsFormat, Locale.getDefault())
            val instant = date.toInstant()
            val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

            formatter.format(localDateTime)
        } else {

            val formatter = SimpleDateFormat(fsFormat, Locale.getDefault())
            formatter.format(date)
        }
    }


}