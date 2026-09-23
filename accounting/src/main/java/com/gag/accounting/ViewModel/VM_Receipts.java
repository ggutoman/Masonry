package com.gag.accounting.ViewModel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Core.Funds;
import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Core.Receipts;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.App.Models.ReceiptMasterList;
import org.gag.appdriver.Constants.RECEIPT_CONSTANTS;
import org.gag.appdriver.Libraries.File.FileHelper;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectDetail;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Receipts extends AndroidViewModel {

    private final Receipts poReceipt;
    private final Annual poAnnuals;
    private final Projects poProjects;
    private final MutableLiveData<String> GetSelectedType = new MutableLiveData<>();
    private final MutableLiveData<List<ReceiptDetail>> GetReceipttDetail = new MutableLiveData<>();

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public interface OnSaveReceipt{
        void OnLoad();
        void OnSuccess(String fsTransaction);
        void OnFailed(String fsMessage);
    }

    public interface OnShareFile{
        void OnLoad();
        void OnSuccess(File file);
        void OnFailed(String fsMessage);
    }

    public interface OnDownload{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public VM_Receipts(@NonNull Application application) {
        super(application);

        poReceipt = new Receipts(application);
        poAnnuals = new Annual(application);
        poProjects = new Projects(application);
    }

    public void SelectType(String fsType){
        GetSelectedType.setValue(fsType);
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

    public String GetFormatLongDate(long fsDate){
        return poReceipt.FormatLongDate(fsDate);
    }

    public String GetCountedDate(int fnCount, int fnDateIndex, boolean fbIsAdd){
        return poReceipt.GetCountedDate(fnCount, fnDateIndex, fbIsAdd);
    }

    public LiveData<String> ObserveSelectType(){
        return GetSelectedType;
    }

    public List<RECEIPT_CONSTANTS> GetSources(){
        return List.of(RECEIPT_CONSTANTS.RECEIPT_ANNUAL, RECEIPT_CONSTANTS.RECEIPT_PROJECT, RECEIPT_CONSTANTS.RECEIPT_BANK, RECEIPT_CONSTANTS.RECEIPT_OTHERS);
    }

    public List<String> GetPaymentTypes(){
        return List.of("Cash", "Check", "Others");
    }

    public EProjectMaster GetProjectMaster(String fsProj){
        return poProjects.GetProjectInfo(fsProj);
    }

    public LiveData<EReceiptMaster> ObserveReceiptMaster(String fsTransNox){
        return poReceipt.ObserveReceiptMaster(fsTransNox);
    }

    public LiveData<List<EReceiptDetail>> ObserveReceiptDetail(String fsTransNox){
        return poReceipt.ObserveReceiptDetail(fsTransNox);
    }

    public LiveData<List<ReceiptDetail>> ObserveReceiptDetail(){
        return GetReceipttDetail;
    }

    public EMemberInfo GetMemberInfo(String fsMemberIDxx){
        return poReceipt.GetMemberInfo(fsMemberIDxx);
    }

    public LiveData<List<AnnualMembers>> GetAnnualReceipts(String fsMemberIDxx){
        return poReceipt.ObserveAnnualReceipts(fsMemberIDxx);
    }

    public LiveData<List<MemberPledges>> GetProjects(String fsMemberIDxx){
        return poReceipt.ObserveProjectReceipts(fsMemberIDxx);
    }

    public EProjectMaster GetProjectInfo(String fsProjctCd){
        return poProjects.GetProjectInfo(fsProjctCd);
    }

    public LiveData<List<ReceiptMasterList>> GetReceiptMembers(String fDfrom, String fDto){
        return poReceipt.ObserveReceiptMembers(fDfrom, fDto);
    }

    public LiveData<List<ReceiptMasterList>> GetReceiptSources(String fsSourceCd, String fDfrom, String fDto){
        return poReceipt.ObserveReceiptSources(fsSourceCd, fDfrom, fDto);
    }

    public void CreateFileFromView(View layoutView, String fileNme, OnShareFile foCallback){

        foCallback.OnLoad();
        poReceipt.CreateFileFromView(layoutView, fileNme).thenAccept(new Consumer<FileHelper.FileResult>() {
            @Override
            public void accept(FileHelper.FileResult fileResult) {
                if (!fileResult.isSuccess()){
                    foCallback.OnFailed(fileResult.getFile().toString());
                    return;
                }
                foCallback.OnSuccess(fileResult.getFile());
            }
        });
    }

    public boolean AddReceiptDetail(String fsTransNox, String fsSourceCd, String fsSourceNo, String fsEntryNox, String fsSourceDescript, String fnAmtPaid){

        List<ReceiptDetail> currentList = GetReceipttDetail.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean exists = false;
        for (ReceiptDetail detail : currentList) {

            if (fsSourceCd.equalsIgnoreCase("DUES") || fsSourceCd.equalsIgnoreCase("PROJ")){

                if (detail.getSSourceCD().equalsIgnoreCase(fsSourceCd) &&
                        detail.getSSourceNo().equalsIgnoreCase(fsSourceNo)) {

                    exists = true;
                    break;
                }

            }
        }
        if (exists) {
            return false;
        }

        ReceiptDetail loReceipt = new ReceiptDetail(
                fsTransNox,
                fsSourceCd,
                fsSourceNo,
                fsEntryNox.isEmpty() ? String.valueOf(currentList.size() + 1) : fsEntryNox,
                fsSourceDescript,
                fnAmtPaid
        );
        currentList.add(loReceipt);

        GetReceipttDetail.setValue(currentList);

        return true;
    }

    public void ReplaceDetail(int fnIndex,  String fnAmtPaid){

        List<ReceiptDetail> currentList = GetReceipttDetail.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        //re initialize with new amount paid the detail
        ReceiptDetail loReceipt = new ReceiptDetail(
                currentList.get(fnIndex).getSTransNox(),
                currentList.get(fnIndex).getSSourceCD(),
                currentList.get(fnIndex).getSSourceNo(),
                currentList.get(fnIndex).getNEntryNox(),
                currentList.get(fnIndex).getSSourceDscrpt(),
                fnAmtPaid
        );
        currentList.set(fnIndex, loReceipt);

        GetReceipttDetail.setValue(currentList);
    }

    public void ClearDetail(){
        GetReceipttDetail.setValue(new ArrayList<>());
    }

    public void DownloadSourceAnnuals(String fsMemberID, OnDownload foCallback){

        foCallback.OnLoad();
        poAnnuals.DownloadAnnualMember(fsMemberID).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poAnnuals.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded annual transactions");
            }
        });
    }

    public void DownloadMemberPledges(String fsMemberID, OnDownload foCallback){

        foCallback.OnLoad();
        poProjects.DownloadProjectMembers(fsMemberID).thenAccept(new Consumer<Object>() {
            @Override
            public void accept(Object result) {

                if (result instanceof Boolean){
                    foCallback.OnFinished(!(Boolean) result ? poProjects.message : "Failed to download projects");
                } else if (result instanceof List) {
                    if (((List<EProjectMaster>) result).size() < 1){
                        foCallback.OnFinished("No project list found");
                    }else {
                        foCallback.OnFinished("Member projects successfully downloaded");
                    }
                }
            }
        });

    }

    public void DownloadReceiptInfo(String fsTransNox, OnDownload foCallback){

        foCallback.OnLoad();
        poReceipt.DownloadReceipt(fsTransNox).thenCompose(result -> {

            if (result instanceof Boolean){
                return CompletableFuture.completedFuture(false);
            } else if (result instanceof List) {

                List<EReceiptDetail> details = (List<EReceiptDetail>) result;
                if (details.size() > 0){

                    CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);
                    for (EReceiptDetail loReceipt : details) {

                        //download each information for source project
                        if (loReceipt.getSSourceCD().equalsIgnoreCase("PROJ")){
                            chain = poProjects.DownloadProjectInfo(loReceipt.getSSourceNo());
                        }
                    }
                    return chain;
                }else {
                    return CompletableFuture.completedFuture(false);
                }
            }
            return CompletableFuture.completedFuture(false);

        }).thenAccept(aBoolean -> {

            if (!aBoolean){
                foCallback.OnFinished("Failed to download receipts");
                return;
            }
            foCallback.OnFinished("Successfully download receipts");
        });
    }

    public void DownloadReceiptList(String fsDfrom, String fsDto, List<String> fatatus, OnDownload foCallback){

        foCallback.OnLoad();
        poReceipt.DownloadReceiptList(fsDfrom, fsDto, fatatus).thenCompose(result -> {

            if (result instanceof Boolean){
                return CompletableFuture.completedFuture(false);
            } else if (result instanceof List) {

                List<EReceiptDetail> details = (List<EReceiptDetail>) result;
                if (details.size() > 0){

                    CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);
                    for (EReceiptDetail loReceipt : details) {

                        //download each information for source project
                        if (loReceipt.getSSourceCD().equalsIgnoreCase("PROJ")){
                            chain = poProjects.DownloadProjectInfo(loReceipt.getSSourceNo());
                        }
                    }
                    return chain;
                }else {
                    return CompletableFuture.completedFuture(false);
                }
            }
            return CompletableFuture.completedFuture(false);
        }).thenAccept(aBoolean -> {

            if (!aBoolean){
                foCallback.OnFinished("Failed to download receipts");
                return;
            }
            foCallback.OnFinished("Successfully download receipts");
        });
    }

    public void SaveReceipt(EReceiptMaster foMaster, List<ReceiptDetail> foDetail, OnSaveReceipt foCallback){

        foCallback.OnLoad();

        //check required prppertoes
        if (foMaster.getSMemberID().isEmpty()) {
            foCallback.OnFailed("Member ID is not initialized");
            return;
        }else if (foMaster.getCPaymForm().isEmpty()){
            foCallback.OnFailed("Payment Type is not initialized");
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
            }else if (foDetail.get(i).getSSourceNo().isEmpty() && (foDetail.get(i).getSSourceCD().equalsIgnoreCase("DUES") || foDetail.get(i).getSSourceCD().equalsIgnoreCase("PROJ"))){
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
                    foDetail.get(i).getNEntryNox(),
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

        poReceipt.SaveReceipt(foMaster, laDetails).thenAccept(o -> {

            if (o instanceof Boolean){
                foCallback.OnFailed(poReceipt.getMessage());
            }else if (o instanceof EReceiptMaster loReceipt){

                if (loReceipt.getSTransNox().isEmpty()){
                    foCallback.OnFailed(poReceipt.getMessage());
                }else {

                    //update receipt as confirmed, new entries are considered payment has been received
                    loReceipt.setCTranStat("1");
                    poReceipt.UpdateReceiptStatus(loReceipt).thenAccept(aBoolean -> {

                        if (!aBoolean){
                            foCallback.OnFailed(poReceipt.getMessage());
                        }else {
                            foCallback.OnSuccess(loReceipt.getSTransNox());
                        }
                    });
                }
            }

        });
    }

    public void UpdateReceiptStatus(EReceiptMaster foMaster, OnTransaction foCallback){

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
