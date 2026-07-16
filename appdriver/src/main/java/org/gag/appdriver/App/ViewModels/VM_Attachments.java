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

    public LiveData<List<EDisbursementAttachment>> GetDisbursements(String fsTransNox){
        return poDisb.GetAttachments(fsTransNox);
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

    public void GenerateSoa(OnGenerate foCallback){

        foCallback.OnLoad();
        poReport.GenerateSOA().thenAccept(new Consumer<Object>() {
            @Override
            public void accept(Object o) {

                if (o instanceof Boolean){
                    foCallback.OnFailed(poReport.GetMessage());
                    return;
                } else if (o instanceof String) {

                    String lsURL = (String) o;
                    if (lsURL == null || lsURL.isEmpty()){
                        foCallback.OnFailed("Failed to generate report");
                    }else {
                        foCallback.OnSuccess(lsURL);
                    }
                }
            }
        });
    }
}
