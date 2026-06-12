package org.gag.appdriver.App.Models

data class OfficerInfo(
    val sYearIDxx: String,   // Officer year ID (foreign key to Lodge_Calendar)
    val nYearxxxx: Int,      // Numeric year value from Lodge_Calendar
    val sPositnDs: String,   // Position description from Position_Info
    val cAppointx: String,   // Appointment flag (e.g., 'Y' or 'N')
    val cStatusxx: String    // Status flag (e.g., '1' for active)
)

