package com.gag.accounting.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.EAnnualDetail;
import org.gag.appdriver.Room.Entities.EAnnualMaster;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.ArrayList;
import java.util.Date;
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

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poAnnual.FormatDateString(fsDate, fsFormat);
    }

    public Date GetStringDate(String fsDate, String fsFormat){
        return poAnnual.ConvertStringDate(fsDate, fsFormat);
    }

    public EUserInfo GetUserInfo(){
        return poAnnual.GetUserInfo();
    }

    public String GetFormatDateString(Date fsDate, String fsFormat){
        return poAnnual.ConvertDateString(fsDate, fsFormat);
    }

    public String GetFormatLongDate(long fsDate){
        return poAnnual.FormatLongDate(fsDate);
    }

    public String GetCurrentDateTime(){
        return poAnnual.GetCurrentDateTime();
    }

    public List<String> GetAmountTypes(){
        return new ArrayList<>(List.of("Amount Due", "Amount Paid"));
    }

    public LiveData<List<AnnualMembers>> GetAnnualDetail(){
        return GetAnnualDetail;
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendars(String fsLodgeIDxx){
        return poAnnual.GetLodgeCalendars(fsLodgeIDxx);
    }

    public LiveData<List<EMemberInfo>> GetMemberList(){
        return poAnnual.ObserveMemberList();
    }

    public LiveData<EAnnualMaster> GetAnnualMaster(String fsYearIDxx){
        return poAnnual.GetAnnualMaster(fsYearIDxx);
    }

    public LiveData<List<AnnualMembers>> GetAnnualDetail(String fsTransNox){
        return poAnnual.GetAnnualDetail(fsTransNox);
    }

    public LiveData<List<EAnnualMaster>> GetAnnualSummary(String fsLodgeIDxx, String fsYearFrom, String fsYearTo){
        return poAnnual.GetAnnualMasterSummary(fsLodgeIDxx, fsYearFrom, fsYearTo);
    }

    public AnnualSummary GetAnnualDetailSummary(String fsTransNox){
        return poAnnual.GetAnnualDetailSummary(fsTransNox);
    }

    public LiveData<List<AnnualSummary>> GetAnnualMemberInfo(){
        return poAnnual.GetAnnualMemberInfo();
    }

    public boolean AddAnnualDetail(String fsTransNox, String fsMemberID, String fsMemberNme, String fsExemptID, String fsRemarksx, String fsAmtDuexx, String fsAmtPaidx){

        AnnualMembers loItem = new AnnualMembers(
                fsTransNox,
                fsMemberID,
                fsMemberNme,
                fsExemptID,
                fsRemarksx,
                fsAmtDuexx,
                fsAmtPaidx
        );

        List<AnnualMembers> currentList = GetAnnualDetail.getValue();

        //do not allow adding of duplicate members
        boolean isAlreadyAdded = false;
        for (AnnualMembers loMember : currentList){
            if (loMember.getSMemberID().equalsIgnoreCase(loItem.getSMemberID())){
                isAlreadyAdded = true;
                break;
            }
        }
        if (isAlreadyAdded) return false;

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loItem);
        GetAnnualDetail.setValue(currentList);

        return true;
    }

    public void ReplaceAnnualDetail(int fnIndex, String fnAmountDue, String fnAmountPaid, String fsRemarks, String fcExempt){

        List<AnnualMembers> currentList = GetAnnualDetail.getValue();

        if (currentList == null) return;

        AnnualMembers loDetail = new AnnualMembers(
                currentList.get(fnIndex).getSTransNox(),
                currentList.get(fnIndex).getSMemberID(),
                currentList.get(fnIndex).getSMemberNme(),
                fcExempt,
                fsRemarks,
                fnAmountDue,
                fnAmountPaid
        );

        currentList.set(fnIndex, loDetail);
        GetAnnualDetail.setValue(currentList);

    }

    public void ClearDetail(){
        GetAnnualDetail.setValue(new ArrayList<>());
    }

    public void DownloadAnnual(String fsLodgeIDxx, String fsYearIDxx, String fsYearFrom, String fsYearTo, OnTransaction foCallback){

        foCallback.OnLoad();
        poAnnual.DownloadAnnualDue(fsLodgeIDxx, fsYearIDxx, fsYearFrom, fsYearTo).thenAccept(new Consumer<Boolean>() {
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

    public void DownloadAnnualMembers(OnTransaction foCallback){

        foCallback.OnLoad();
        poAnnual.DownloadAnnualMember().thenAccept(new Consumer<Boolean>() {
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

    public void SaveAnnualDue(EAnnualMaster foMaster, List<AnnualMembers> foDetail, OnTransaction foCallback){

        try {

            foCallback.OnLoad();

            double ldbl_trantotal = 0.00;
            double ldbl_collectotal = 0.00;

            List<EAnnualDetail> loDetail = new ArrayList<>();
            for (int i = 0; i < foDetail.size(); i++){

                loDetail.add(
                        new EAnnualDetail(
                                foDetail.get(i).getSTransNox(),
                                String.valueOf(i),
                                foDetail.get(i).getSMemberID(),
                                foDetail.get(i).getNAmtDuexx(),
                                foDetail.get(i).getNAmtPaidx(),
                                foDetail.get(i).getCExemptID(),
                                foDetail.get(i).getSRemarksx(),
                                poAnnual.GetCurrentDate(),
                                poAnnual.GetCurrentDateTime()
                        )
                );

                ldbl_trantotal += Double.parseDouble(loDetail.get(i).getNAmtDuexx());
                ldbl_collectotal += Double.parseDouble(loDetail.get(i).getNAmtPaidx());
            }

            //initialize master total from collected details
            foMaster.setNTranTotl(String.valueOf(ldbl_trantotal));
            foMaster.setNCollTotl(String.valueOf(ldbl_collectotal));

            poAnnual.SaveAnnualDue(foMaster, loDetail).thenAccept(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {

                    if (!aBoolean){
                        foCallback.OnFailed(poAnnual.getMessage());
                        return;
                    }
                    foCallback.OnSuccess();
                }
            });

        }catch (Exception e){
            foCallback.OnFailed(e.getMessage());
        }
    }

    public void ApproveAnnualDue(EAnnualMaster foMaster, OnTransaction foCallback){

        try {

            foCallback.OnLoad();

            poAnnual.ApproveAnnualDue(foMaster).thenAccept(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) {

                    if (!aBoolean){
                        foCallback.OnFailed(poAnnual.getMessage());
                        return;
                    }
                    foCallback.OnSuccess();
                }
            });

        }catch (Exception e){
            foCallback.OnFailed(e.getMessage());
        }
    }
}
