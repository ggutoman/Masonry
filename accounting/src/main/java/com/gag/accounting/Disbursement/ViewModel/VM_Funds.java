package com.gag.accounting.Disbursement.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.Entities.EFundTurnOver;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.List;
import java.util.function.Consumer;

public class VM_Funds extends AndroidViewModel {

    private final Funds poFunds;
    private final DateRepository poDate = new DateRepository();

    public interface OnSubmit{
        void OnLoad();
        void OnSucces();
        void OnFailed(String fsMessage);
    }

    public VM_Funds(@NonNull Application application) {
        super(application);

        poFunds = new Funds(application);
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendars(){
        return poFunds.ObserveLodgeCalendarList();
    }

    public LiveData<EFundTurnOver> ObserveFundTurnovers(String fsTransox){
        return poFunds.ObserveTurnover(fsTransox);
    }

    public LiveData<List<EFundTurnOver>> ObserveFundTurnoverList( String fsYearID, String fsDfrom, String fsDto){
        return poFunds.ObserveTurnoverList(fsYearID, fsDfrom, fsDto);
    }

    public EUserInfo GetUserInfo(){
        return poFunds.GetUserInfo();
    }

    public String GetCurrentDate(){
        return poFunds.GetCurrentDate();
    }

    public String GetCurrentDateTime(){
        return poFunds.GetCurentDateTime();
    }

    public String GetFormattedDate(Long flDate){
        return poDate.FormatLongDate(flDate);
    }

    public void CreateFundTurnover(EFundTurnOver foTurnover, OnSubmit foCallback) {

        foCallback.OnLoad();

        poFunds.CreateFundTurnover(foTurnover).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poFunds.getMessage());
                    return;
                }
                foCallback.OnSucces();
            }
        }).exceptionally(throwable -> {
            foCallback.OnFailed("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void UpdateFundTurnover(EFundTurnOver foTurnover, OnSubmit foCallback) {

        foCallback.OnLoad();

        poFunds.UpdateFundTurnover(foTurnover).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poFunds.getMessage());
                    return;
                }
                foCallback.OnSucces();
            }
        }).exceptionally(throwable -> {
            foCallback.OnFailed("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void ApproveFundTurnover(EFundTurnOver foTurnover, OnSubmit foCallback) {

        foCallback.OnLoad();

        poFunds.ApproveFundTurnover(foTurnover).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poFunds.getMessage());
                    return;
                }
                foCallback.OnSucces();
            }
        }).exceptionally(throwable -> {
            foCallback.OnFailed("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void DownloadFunds(String fsYearID, String fsDfrom, String fsDto, OnSubmit focallBack){

        focallBack.OnLoad();
        poFunds.DownloadFundHistory(fsYearID, fsDfrom, fsDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    focallBack.OnFailed(poFunds.getMessage());
                    return;
                }
                focallBack.OnSucces();
            }
        });
    }
}
