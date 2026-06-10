package com.gag.accounting.Disbursement.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.Room.DataObject.DLodgeCalendar;

import java.util.List;

public class VM_Funds extends AndroidViewModel {

    private Funds poFunds;

    public VM_Funds(@NonNull Application application) {
        super(application);

        poFunds = new Funds(application);
    }

    public LiveData<List<DLodgeCalendar.LodgeCalendarList>> GetLodgeCalendars() {
        return poFunds.getPoLodgeCalendar().GetLodgeCalendarList();
    }
}
