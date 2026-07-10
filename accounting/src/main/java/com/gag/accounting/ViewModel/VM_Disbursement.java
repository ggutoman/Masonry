package com.gag.accounting.ViewModel;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Core.Disbursement;
import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Core.Receipts;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.App.Models.ReceiptMasterList;
import org.gag.appdriver.Constants.DISBURSEMENT_CONSTANTS;
import org.gag.appdriver.Constants.RECEIPT_CONSTANTS;
import org.gag.appdriver.Libraries.File.FileHelper;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Room.Entities.EFundMaster;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VM_Disbursement extends AndroidViewModel {

    private final Disbursement poDisbursement;
    private final Funds poFunds;
    private final Projects poProjects;
    private final MutableLiveData<String> GetSelectedType = new MutableLiveData<>();
    private final MutableLiveData<List<ReceiptDetail>> GetReceipttDetail = new MutableLiveData<>();

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public VM_Disbursement(@NonNull Application application) {
        super(application);

        poDisbursement = new Disbursement(application);
        poFunds = new Funds(application);
        poProjects = new Projects(application);
    }

    public String GetUserID(){
        return poDisbursement.GetUserID();
    }

    public String GetLodgeID(){
        return poDisbursement.GetLodgeID();
    }

    public String GetCurrentDate(){
        return poDisbursement.GetCurrentDate();
    }

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poDisbursement.FormatDateString(fsDate, fsFormat);
    }

    public String GetFormatLongDate(long fsDate){
        return poDisbursement.FormatLongDate(fsDate);
    }

    public String GetCountedDate(int fnCount, int fnDateIndex, boolean fbIsAdd){
        return poDisbursement.GetCountedDate(fnCount, fnDateIndex, fbIsAdd);
    }

    public List<DISBURSEMENT_CONSTANTS> GetSources(){
        return List.of(DISBURSEMENT_CONSTANTS.DISBURSEMENT_FUNDS, DISBURSEMENT_CONSTANTS.DISBURSEMENT_PROJECT);
    }

    public void SelectType(String fsType){
        GetSelectedType.setValue(fsType);
    }

    public LiveData<String> ObserveSelectType(){
        return GetSelectedType;
    }

    public LiveData<List<LodgeInfo>> ObserveLodgeList(){
        return poFunds.ObserveLodgeList();
    }

    public LiveData<List<EProjectMaster>> GetLodgeProjects(String fsLodgeIDxx){
        return poProjects.GetLodgeProjects(fsLodgeIDxx);
    }

    public LiveData<List<LodgeFundMasterList>> GetLodgeFunds(String fsLodgeIDxx){
        return poFunds.ObserveLodgeFunds(fsLodgeIDxx);
    }

    public LiveData<EDisbursement> GetDisbursement(String fsTransNox){
        return poDisbursement.GetDisbursement(fsTransNox);
    }

    public LiveData<List<EDisbursement>> GetDisbursementList(String fsDfromxx, String fsDtoxx){
        return poDisbursement.GetDisbursementList(fsDfromxx, fsDtoxx);
    }

    public void DownloadProjects(String fsLodgeIDxx, VM_Receipts.OnDownload foCallback){

        foCallback.OnLoad();
        poProjects.DownloadLodgeProjects(fsLodgeIDxx).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poProjects.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded projects");
            }
        });

    }

    public void DownloadLodgeFunds(String fsLodgeIDxx, VM_Receipts.OnDownload foCallback){

        foCallback.OnLoad();
        poFunds.DownloadFundMasterList(fsLodgeIDxx).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poProjects.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded projects");
            }
        });

    }

    public void SaveDisbursement(EDisbursement loDisbursement, VM_Disbursement.OnTransaction foCallback){

        foCallback.OnLoad();

        //double check if parameter is initialized
        if (loDisbursement.getDTransact().isEmpty()){
            foCallback.OnFailed("Transaction date is not initialized");
            return;
        }else if (loDisbursement.getCTranStat().isEmpty()){
            foCallback.OnFailed("Transaction status is not initialized");
            return;
        }else if (loDisbursement.getSPayeeNme().isEmpty()){
            foCallback.OnFailed("Payee is not initialized");
            return;
        }else if (loDisbursement.getSParticlr().isEmpty()){
            foCallback.OnFailed("Particulars is not initialized");
            return;
        }else if (loDisbursement.getSVouchrNo().isEmpty()){
            foCallback.OnFailed("Voucher number is not initialized");
            return;
        }else if (loDisbursement.getSSourceCD().isEmpty()){
            foCallback.OnFailed("Source code is not initialized");
            return;
        }else if (loDisbursement.getSSourceNo().isEmpty()){
            foCallback.OnFailed("Source transaction is not initialized");
            return;
        }else if (loDisbursement.getNAmountxx().isEmpty()){
            foCallback.OnFailed("Source amount is not initialized");
            return;
        }

        poDisbursement.SaveDisbursement(loDisbursement).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFailed(poDisbursement.getMessage());
                    return;
                }
                foCallback.OnSuccess();
            }
        });
    }

    public void DownloadDisbursementList(String fsDfromxx, String fsDtoxx, List<String> laStatus, VM_Receipts.OnDownload foCallback){

        foCallback.OnLoad();
        poDisbursement.DownloadDisbursementList(fsDfromxx, fsDtoxx, laStatus).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDisbursement.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded receipts");
            }
        });
    }

    public void DownloadDisbursementInfo(String fsTransNox, VM_Receipts.OnDownload foCallback){

        foCallback.OnLoad();
        poDisbursement.DownloadDisbursementInfo(fsTransNox).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDisbursement.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded disbursements");
            }
        });
    }

}
