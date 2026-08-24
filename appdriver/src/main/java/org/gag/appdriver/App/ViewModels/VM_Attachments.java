package org.gag.appdriver.App.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.AttachmentReport;
import org.gag.appdriver.App.Core.Disbursement;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EReports;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VM_Attachments extends AndroidViewModel {

    private final DateRepository poDate;
    private final Disbursement poDisb;
    private final AttachmentReport poReport;

    public interface OnDownload{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public interface OnGetReports{
        void OnLoad();
        void OnSuccess(List<File> faFiles);
        void OnFailed(String fsMessage);
    }

    public interface OnGenerate{
        void OnLoad();
        void OnSuccess(String lsURL);
        void OnFailed(String fsMessage);
    }

    public VM_Attachments(@NonNull Application application) {
        super(application);

        poDate = new DateRepository();
        poDisb = new Disbursement(application);
        poReport = new AttachmentReport(application);
    }

    public String GetCurrentDate(){
        return poDate.GetCurrentDate();
    }

    public LiveData<List<EDisbursementAttachment>> GetDisbursements(String fsTransNox){
        return poDisb.GetAttachments(fsTransNox);
    }

    public LiveData<EReports> GetReportInfo(String fsIDxx){
        return poReport.GetReportInfo(fsIDxx);
    }

    public Boolean IsDateCompared(String fsDfrom, String fsDto){
        return poDate.IsDateCompared(fsDfrom, fsDto);
    }

    public String FormatLongDate(Long fLongDte){
        return poDate.FormatLongDate(fLongDte);
    }

    public void DownloadDVAttachment(String fsTransNox, OnDownload foCallback){

        foCallback.OnLoad();
        poDisb.DownloadAttachmentList(fsTransNox).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDisb.getMessage());
                    return;
                }
                foCallback.OnFinished("Successfully downloaded attachments");
            }
        });
    }

    public void GenerateReport(String fsIDxx, String fsUrl, JSONObject foParam, OnGenerate foCallback){

        foCallback.OnLoad();
        poReport.GenerateReport(fsIDxx, fsUrl, foParam).thenAccept(new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o instanceof Boolean){
                    foCallback.OnFailed(poReport.GetMessage());
                } else if (o instanceof String) {

                    String lsURL = (String) o;
                    if (lsURL == null || lsURL.isEmpty() || !new File(lsURL).exists()){
                        foCallback.OnFailed("Failed to generate report");
                    }else {
                        foCallback.OnSuccess(lsURL);
                    }
                }
            }
        });
    }

    public void DownloadReports(OnDownload foCallback){

        foCallback.OnLoad();
        poReport.DownloadReportObjects().thenAccept(isSuccess -> {
            foCallback.OnFinished(isSuccess ? "Successfully downloaded reports" : poReport.message);
        });
    }

    public LiveData<List<EReports>> GetObjects(){
        return poReport.GetObjects();
    }

    public void GetReportList(String fsIDxx, OnGetReports foCallback){

        foCallback.OnLoad();

       poReport.GetReportList(fsIDxx).thenAccept(new Consumer<List<File>>() {
            @Override
            public void accept(List<File> files) {

                if (files == null || files.size() < 1){
                    foCallback.OnFailed("Failed to retrieve reports");
                    return;
                }
                foCallback.OnSuccess(files);
            }
        });
    }
}
