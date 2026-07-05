package com.gag.accounting.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Receipts;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.Constants.RECEIPT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectDetail;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Receipts extends AndroidViewModel {

    private final Receipts poReceipt;
    private final MutableLiveData<String> GetSelectedReceipt = new MutableLiveData<>();
    private final MutableLiveData<List<ReceiptDetail>> GetReceipttDetail = new MutableLiveData<>();

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnDownload{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public VM_Receipts(@NonNull Application application) {
        super(application);

        poReceipt = new Receipts(application);
    }

    public String GetUserID(){
        return poReceipt.GetUserID();
    }

    public String GetCurrentDate(){
        return poReceipt.GetCurrentDate();
    }

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poReceipt.FormatDateString(fsDate, fsFormat);
    }

    public String GetCurrentDateTime(){
        return poReceipt.GetCurrentDateTime();
    }

    public List<RECEIPT_CONSTANTS> GetSources(){
        return List.of(RECEIPT_CONSTANTS.RECEIPT_ANNUAL, RECEIPT_CONSTANTS.RECEIPT_PROJECT, RECEIPT_CONSTANTS.RECEIPT_BANK, RECEIPT_CONSTANTS.RECEIPT_OTHERS);
    }

    public List<String> GetPaymentTypes(){
        return List.of("Cash", "Check", "Others");
    }

    public LiveData<EReceiptMaster> ObserveReceiptMaster(String fsTransNox){
        return poReceipt.ObserveReceiptMaster(fsTransNox);
    }

    public void SelectTransaction(String fsType){
        GetSelectedReceipt.setValue(fsType);
    }

    public LiveData<String> ObserveSelectTransaction(){
        return GetSelectedReceipt;
    }

    public LiveData<List<EReceiptDetail>> ObserveReceiptDetail(String fsTransNox){
        return poReceipt.ObserveReceiptDetail(fsTransNox);
    }

    public EMemberInfo GetMemberInfo(String fsMemberIDxx){
        return poReceipt.GetMemberInfo(fsMemberIDxx);
    }

    public LiveData<List<AnnualMembers>> GetAnnualReceipts(String fsMemberIDxx){
        return poReceipt.ObserveAnnualReceipts(fsMemberIDxx);
    }

    public String GetFormatLongDate(long fsDate){
        return poReceipt.FormatLongDate(fsDate);
    }

    public String GetCountedDate(int fnCount, int fnDateIndex, boolean fbIsAdd){
        return poReceipt.GetCountedDate(fnCount, fnDateIndex, fbIsAdd);
    }

    public boolean AddReceiptDetail(String fsTransNox, String fsSourceCd, String fsSourceNo, String fnAmtPaid){

        ReceiptDetail loReceipt = new ReceiptDetail(
                fsTransNox,
                fsSourceCd,
                fsSourceNo,
                fnAmtPaid
        );

        List<ReceiptDetail> currentList = GetReceipttDetail.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean exists = false;
        for (ReceiptDetail detail : currentList) {

            if (detail.getSSourceCD().equalsIgnoreCase(fsSourceCd) &&
                    detail.getSSourceNo().equalsIgnoreCase(fsSourceNo)) {

                exists = true;
                break;
            }
        }
        if (exists) {
            return false;
        }
        currentList.add(loReceipt);

        GetReceipttDetail.setValue(currentList);

        return true;
    }

    public void ClearDetail(){
        GetReceipttDetail.setValue(new ArrayList<>());
    }

    public void SaveReceipt(EReceiptMaster foMaster, List<ReceiptDetail> foDetail, OnTransaction foCallback){

        foCallback.OnLoad();

        //check required prppertoes
        if (foMaster.getSMemberID().isEmpty()) {
            foCallback.OnFailed("Member ID is not initialized");
            return;
        }else if (foMaster.getCPaymForm().isEmpty()){
            foCallback.OnFailed("Payment Type is not initialized");
            return;
        }else if (foMaster.getSORNoxxxx().isEmpty()){
            foCallback.OnFailed("OR Number is not initialized");
            return;
        }else if (foMaster.getCTranStat().isEmpty()){
            foCallback.OnFailed("Status not initialized");
            return;
        }else if (foDetail.size() < 1){
            foCallback.OnFailed("Please add atleast one detail");
            return;
        }

        //initialize details to saving parameter and compute total for master
        List<EReceiptDetail> laDetails = new ArrayList<>();
        double ldbl_paid = 0.00;

        boolean isDetailsOkay = true;
        for (int i = 0; i < foDetail.size(); i++){

            //check each details property
            if (foDetail.get(i).getSSourceCD().isEmpty()){
                foCallback.OnFailed("Source is not initialized");
                isDetailsOkay = false;
                break;
            }else if (foDetail.get(i).getSSourceNo().isEmpty()){
                foCallback.OnFailed("Source number is not initialized");
                isDetailsOkay = false;
                break;
            }else if (foDetail.get(i).getNAmtPaidx().isEmpty()){
                foCallback.OnFailed("Paid Amount is not initialized");
                isDetailsOkay = false;
                break;
            }

            EReceiptDetail loParam = new EReceiptDetail(
                    foDetail.get(i).getSTransNox(),
                    String.valueOf(i),
                    foDetail.get(i).getSSourceCD(),
                    foDetail.get(i).getSSourceNo(),
                    foDetail.get(i).getNAmtPaidx(),
                    GetCurrentDate(),
                    GetCurrentDateTime()
            );
            laDetails.add(loParam);

            ldbl_paid += Double.parseDouble(foDetail.get(i).getNAmtPaidx());
        }

        //do not proceed if details not initialized properly
        if (!isDetailsOkay){
            return;
        }
        foMaster.setNTotlPaid(String.valueOf(ldbl_paid));

        poReceipt.SaveReceipt(foMaster, laDetails).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poReceipt.getMessage());
                }else{
                    foCallback.OnSuccess();
                }
            }
        });
    }

    public void UpdateProjectStatus(EReceiptMaster foMaster, OnTransaction foCallback){

        foCallback.OnLoad();

        poReceipt.UpdateReceiptStatus(foMaster).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poReceipt.getMessage());
                }else{
                    foCallback.OnSuccess();
                }
            }
        });
    }
}
