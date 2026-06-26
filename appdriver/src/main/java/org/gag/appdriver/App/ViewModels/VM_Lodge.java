package org.gag.appdriver.App.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.App.Core.Lodge;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.Entities.EFundLedger;
import org.gag.appdriver.Room.Entities.ELodgeCalendar;
import org.gag.appdriver.Room.Entities.ELodgeInfo;

import java.util.List;
import java.util.function.Consumer;

public class VM_Lodge extends AndroidViewModel {

    private final Lodge poLodge;
    private final Funds poFunds;

    public interface OnDownload{
        void OnLoad();
        void OnSuccess();
        void OnError(String fsMessage);
    }

    public VM_Lodge(@NonNull Application application) {
        super(application);

        poLodge = new Lodge(application);
        poFunds = new Funds(application);
    }

    public String GetFormatDate(String fsDate, String fsFormat){
        return poFunds.GetFormattedDate(fsDate, fsFormat);
    }

    public ELodgeInfo GetLodgeInfo(String fsLodgeIDx){
        return poFunds.GetLodgeInfo(fsLodgeIDx);
    }

    public TownProvince GetTownInfo(String fsSearch){
        return poLodge.GetTownInfo(fsSearch);
    }

    public LiveData<ELodgeInfo> ObserveLodgeInfo(String fsLodgeIDx){
        return poFunds.ObserveLodgeInfo(fsLodgeIDx);
    }

    public LiveData<List<LodgeInfo>> GetLodgeList() {
        return poLodge.GetLodges();
    }

    public LiveData<List<TownProvince>> SearchTown(String fsSearch){
        return poLodge.SearchTown(fsSearch);
    }

    public LiveData<List<EFundLedger>> GetFundLedger(String fsLodgeIDx, String fsDfrom, String fsDto){
        return poFunds.ObserveLedgers(fsLodgeIDx, fsDfrom, fsDto);
    }

    public LiveData<ELodgeCalendar> GetLodgeCalendarInfo(String sYearIDx){
        return poLodge.GetLodgeCalendarInfo(sYearIDx);
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendarList(String fsLodgeIDx, String fsDateFrom, String fsDateTo){
        return poLodge.GetLodgeCalendarList(fsLodgeIDx, fsDateFrom, fsDateTo);
    }

    public String GetCurrentDate(){
        return poLodge.GetCurrentDate();
    }

    public String GetFirstQuarter(){
        return poLodge.GetCountDate(4, 0, false);
    }

    public String GetCurrentDateTime(){
        return poLodge.GetCurrentDateTime();
    }

    public String GetFormattedDate(Long flDate){
        return poLodge.GetFormattedLongDate(flDate);
    }

    public boolean IsDateCompared(String fsDate1, String fsDate2){
        return poLodge.IsDateCompared(fsDate1, fsDate2);
    }

    public void CreateLodge(ELodgeInfo foLodge, VM_Lodge.OnDownload foCallback){

        foCallback.OnLoad();

        poLodge.CreateLodge(foLodge).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnError(poLodge.GetMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }

    public void CreateLodgeCalendar(ELodgeCalendar foLodgeCalendar, VM_Lodge.OnDownload foCallback){

        foCallback.OnLoad();

        poLodge.CreateLodgeCalendar(foLodgeCalendar).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnError(poLodge.GetMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }

    public void DownloadLodgeCalendars(String fsDateFrom, String fsDateTo, OnDownload loCallback){

        loCallback.OnLoad();
        poLodge.DownloadLodgeCalendars(fsDateFrom, fsDateTo).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    loCallback.OnError(poLodge.GetMessage());
                    return;
                }
                loCallback.OnSuccess();
            }
        });
    }

    public void DownloadLodgeFunds(String fsDfrom, String fsDto, OnDownload foCallback){

        foCallback.OnLoad();
        poFunds.DownloadFundMasterLedger(fsDfrom, fsDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnError(poFunds.getMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }
}
