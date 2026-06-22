package com.gag.accounting.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.Entities.EAnnualMaster;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VM_Annual extends AndroidViewModel {

    private final Annual poAnnual;
    private final MutableLiveData<List<AnnualMembers>> GetAnnualDetail = new MutableLiveData<>();

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public VM_Annual(@NonNull Application application) {
        super(application);

        poAnnual = new Annual(application);
    }

    public String GetUserID(){
        return poAnnual.GetUserID();
    }

    public String GetCurrentDate(){
        return poAnnual.GetCurrentDate();
    }

    public String GetCurrentDateTime(){
        return poAnnual.GetCurrentDateTime();
    }

    public void AddAnnualDetail(String fsMemberID, String fsMemberNme, String fsExemptID, String fsRemarksx, String fsAmtDuexx, String fsAmtPaidx){

        AnnualMembers loItem = new AnnualMembers(
                fsMemberID,
                fsMemberNme,
                fsExemptID,
                fsRemarksx,
                fsAmtDuexx,
                fsAmtPaidx
        );

        List<AnnualMembers> currentList = GetAnnualDetail.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loItem);
        GetAnnualDetail.setValue(currentList);
    }

    public void ClearDetail(){
        GetAnnualDetail.setValue(new ArrayList<>());
    }

    public LiveData<List<AnnualMembers>> GetAnnualDetail(){
        return GetAnnualDetail;
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendars(String fsLodgeIDxx){
        return poAnnual.GetLodgeCalendars(fsLodgeIDxx);
    }

    public LiveData<EAnnualMaster> GetAnnualMaster(String fsYearIDxx){
        return poAnnual.GetAnnualMaster(fsYearIDxx);
    }

    public LiveData<List<AnnualMembers>> GetAnnualDetail(String fsTransNox){
        return poAnnual.GetAnnualDetail(fsTransNox);
    }

    public void DownloadAnnual(String fsYearIDxx, OnTransaction foCallback){

        foCallback.OnLoad();
        poAnnual.DownloadAnnualDue(fsYearIDxx).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poAnnual.getMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }
}
