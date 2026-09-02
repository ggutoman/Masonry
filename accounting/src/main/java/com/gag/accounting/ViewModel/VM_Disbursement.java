package com.gag.accounting.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Disbursement;
import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Constants.DISBURSEMENT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Disbursement extends AndroidViewModel {

    private final Disbursement poDisbursement;
    private final Funds poFunds;
    private final Projects poProjects;
    private final MutableLiveData<String> GetSelectedType = new MutableLiveData<>();
    private final MutableLiveData<List<EDisbursementAttachment>> GetAttachedFile = new MutableLiveData<>();

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

    public LiveData<String> ObserveSelectType(){
        return GetSelectedType;
    }

    public LiveData<List<EDisbursementAttachment>> ObserveAttachedFile(){
        return GetAttachedFile;
    }

    public LiveData<LodgeFundMasterList> ObserveLodgeFund(String fsLodgeIDxx){
        return poFunds.ObserveLodgeFund(fsLodgeIDxx);
    }

    public LiveData<List<EProjectMaster>> GetLodgeProjects(String fsLodgeIDxx){
        return poProjects.GetLodgeProjects(fsLodgeIDxx);
    }

    public LiveData<EDisbursement> GetDisbursement(String fsTransNox){
        return poDisbursement.GetDisbursement(fsTransNox);
    }

    public LiveData<List<EDisbursement>> GetDisbursementList(String fsDfromxx, String fsDtoxx){
        return poDisbursement.GetDisbursementList(fsDfromxx, fsDtoxx);
    }

    public LiveData<List<EDisbursementAttachment>> GetAttachments(String fsTransNox){
        return poDisbursement.GetAttachments(fsTransNox);
    }

    public void SelectType(String fsType){
        GetSelectedType.setValue(fsType);
    }

    public void AttachFile(String fsTransNox, String fsSourceNo, String fsDocTitle, String fsRemarks, File foFile, String fcRecdStat){

        EDisbursementAttachment dvAttachment = new EDisbursementAttachment(
                fsTransNox,
                fsSourceNo,
                GetCurrentDate(),
                fsDocTitle,
                fsRemarks,
                foFile.getName(),
                foFile.getAbsolutePath(),
                fcRecdStat,
                GetUserID(),
                GetCurrentDate()
        );

        List<EDisbursementAttachment> currentList = GetAttachedFile.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(dvAttachment);

        GetAttachedFile.setValue(currentList);
    }

    public void ReplaceAttachment(int fnIndex, String fsDocTitlex, String fsRemarksx){

        List<EDisbursementAttachment> currentList = GetAttachedFile.getValue();

        if (currentList == null) return;
        EDisbursementAttachment loDetail = new EDisbursementAttachment(
                currentList.get(fnIndex).getSTransNox(),
                currentList.get(fnIndex).getSSourceNo(),
                currentList.get(fnIndex).getDTransact(),
                fsDocTitlex,
                fsRemarksx,
                currentList.get(fnIndex).getSFileName(),
                currentList.get(fnIndex).getSFinFleNm(),
                currentList.get(fnIndex).getCRecdStat(),
                GetUserID(),
                GetCurrentDate()
        );

        currentList.set(fnIndex, loDetail);
        GetAttachedFile.setValue(currentList);

    }

    public void ClearAttachments(){
        GetAttachedFile.setValue(new ArrayList<>());
    }

    public void GenerateFileImage(VM_Receipts.OnShareFile foCallback){

        foCallback.OnLoad();
        poDisbursement.GenerateFileOutput("DV" + poDisbursement.GetFormattedDateTime(poDisbursement.GetCurrentDateTime(), "yyyyMMddHmmss") + ".png").thenAccept(new Consumer<File>() {
            @Override
            public void accept(File file) {
                if (file.getName().isEmpty()){
                    foCallback.OnFailed("Failed to generate a file");
                    return;
                }
                foCallback.OnSuccess(file);
            }
        });
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
                foCallback.OnFinished("Successfully downloaded lodge funds");
            }
        });

    }

    public void SaveDisbursement(EDisbursement loDisbursement, List<EDisbursementAttachment> faAttachments, VM_Disbursement.OnTransaction foCallback){

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

        //run first background to get the result of transaction number
        poDisbursement.SaveDisbursement(loDisbursement).thenCompose(result -> {

            //if return is booleen
            if (result instanceof Boolean && !(Boolean) result) {
                foCallback.OnFailed(poDisbursement.getMessage());
                return CompletableFuture.completedFuture(false);
            }

            //if return is empty string
            if (result instanceof String && (((String) result).isEmpty())) {
                foCallback.OnFailed("Disbursement is not saved properly");
                return CompletableFuture.completedFuture(false);
            }
            String lsSourceno = (String) result;

            //get information of saved disbursement
            EDisbursement loDisb = poDisbursement.GetDisbursementInfo(lsSourceno);
            if (loDisb == null || loDisb.getSTransNox().isEmpty()){
                return CompletableFuture.completedFuture(false);
            }

            //save attachments
            CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);
            for (EDisbursementAttachment loAttachment : new HashSet<>(faAttachments)) {

                loAttachment.setSSourceNo(lsSourceno);

                //bind the result after each thread runs
                chain = chain.thenCompose(ok -> {

                    //continue saving attachment if previous is success
                    if (ok) poDisbursement.SaveAttachment(loAttachment);
                    return CompletableFuture.completedFuture(ok);
                });
            }

            chain = chain.thenCompose(aBoolean -> {

                if (aBoolean){

                    //update disbursement status into release, consider every entry has released a fund
                    loDisb.setCTranStat("2");
                    return poDisbursement.UpdateDisbursementStatus(loDisb);
                }
                return CompletableFuture.completedFuture(aBoolean);
            });

            return chain;

        })
        .thenAccept(allOk -> {
            if (allOk) {
                foCallback.OnSuccess();
            } else {
                foCallback.OnFailed(poDisbursement.getMessage());
            }
        })
        .exceptionally(e -> {
            foCallback.OnFailed("Could not make request at this moment:\n\n" + e.getMessage());
            return null;
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
                foCallback.OnFinished("Successfully downloaded disbursement");
            }
        });
    }

    public void DownloadDisbursementInfo(String fsTransNox, VM_Receipts.OnDownload foCallback){

        foCallback.OnLoad();
        poDisbursement.DownloadDisbursementInfo(fsTransNox).thenCompose(aBoolean -> {

            CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(aBoolean);
            chain = chain.thenCompose(ok -> {
                if (!ok) return CompletableFuture.completedFuture(false);
                return poDisbursement.DownloadAttachmentList(fsTransNox);
            });

            return chain;
        }).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDisbursement.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded disbursement");
            }
        })
        .exceptionally(throwable -> {
            foCallback.OnFinished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void UpdateStatus(EDisbursement foDisb, OnTransaction foCallback){

        foCallback.OnLoad();
        poDisbursement.UpdateDisbursementStatus(foDisb).thenAccept(new Consumer<Boolean>() {
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

    public void SaveAttachment(EDisbursementAttachment foAttachment, VM_Disbursement.OnTransaction foCallback){

        foCallback.OnLoad();

        //double check if parameter is initialized
        if (foAttachment.getSDocTitle().isEmpty()){
            foCallback.OnFailed("Document title is not initialized");
            return;
        }else if (foAttachment.getSFileName().isEmpty()){
            foCallback.OnFailed("Filename is not initialized");
            return;
        }else if (foAttachment.getSFinFleNm().isEmpty()){
            foCallback.OnFailed("File path is not initialized");
            return;
        }

        poDisbursement.SaveAttachment(foAttachment).thenAccept(new Consumer<Boolean>() {
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

    public void DownloadAttachment(String fsTransNox, OnTransaction foCallback){

        foCallback.OnLoad();
        poDisbursement.DownloadAttachmentDV(fsTransNox).thenAccept(new Consumer<Boolean>() {
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
}
