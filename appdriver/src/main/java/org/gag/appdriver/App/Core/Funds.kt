package org.gag.appdriver.App.Core

import android.content.Context
import org.gag.appdriver.Room.DataObject.DLodgeCalendar
import org.gag.appdriver.Room.ML_DBF

class Funds(instance : Context) {

    val poLodgeCalendar : DLodgeCalendar = ML_DBF.getDatabase(instance)?.GetLodgeCalendar() as DLodgeCalendar
}