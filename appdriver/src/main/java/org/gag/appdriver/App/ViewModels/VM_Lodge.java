package org.gag.appdriver.App.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.LodgeCalendar;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.ELodgeCalendar;
import org.gag.appdriver.Room.Entities.ELodgeInfo;

import java.util.List;
import java.util.function.Consumer;

public class VM_Lodge extends AndroidViewModel {

    private final LodgeCalendar poLodgeYear;

    public interface OnDownload{
        void OnLoad();
        void OnSuccess();
        void OnError(String fsMessage);
    }

    public VM_Lodge(@NonNull Application application) {
        super(application);

        poLodgeYear = new LodgeCalendar(application);
    }

    public LiveData<List<ELodgeInfo>> GetLodgeList() {
        return poLodgeYear.GetLodges();
    }

    public LiveData<ELodgeCalendar> GetLodgeCalendarInfo(String sYearIDx){
        return poLodgeYear.GetLodgeCalendarInfo(sYearIDx);
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendarList(String fsDateFrom, String fsDateTo){
        return poLodgeYear.GetLodgeCalendarList(fsDateFrom, fsDateTo);
    }

    public String GetCurrentDate(){
        return poLodgeYear.GetCurrentDate();
    }

    public String GetFirstQuarter(){
        return poLodgeYear.GetCountDate(4, 0, false);
    }

    public String GetCurrentDateTime(){
        return poLodgeYear.GetCurrentDateTime();
    }

    public String GetFormattedDate(Long flDate){
        return poLodgeYear.GetFormattedLongDate(flDate);
    }

    public boolean IsDateCompared(String fsDate1, String fsDate2){
        return poLodgeYear.IsDateCompared(fsDate1, fsDate2);
    }

    public void CreateLodgeCalendar(ELodgeCalendar foLodgeCalendar, VM_Lodge.OnDownload foCallback){

        foCallback.OnLoad();

        poLodgeYear.CreateLodgeCalendar(foLodgeCalendar).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnError(poLodgeYear.GetMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }

    public void DownloadLodgeCalendars(String fsDateFrom, String fsDateTo, OnDownload loCallback){

        loCallback.OnLoad();
        poLodgeYear.DownloadLodgeCalendars(fsDateFrom, fsDateTo).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    loCallback.OnError(poLodgeYear.GetMessage());
                    return;
                }
                loCallback.OnSuccess();
            }
        });
    }
}
