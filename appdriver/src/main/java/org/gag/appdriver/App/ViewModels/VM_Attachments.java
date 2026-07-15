package org.gag.appdriver.App.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.Disbursement;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Room.Entities.EDisbursementAttachment;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class VM_Attachments extends AndroidViewModel {

    private final DateRepository poDate;
    private final Disbursement poDisb;

    public interface OnDownload{
        void OnLoad();
        void OnFinished(String fsMessage);
    }

    public VM_Attachments(@NonNull Application application) {
        super(application);

        poDate = new DateRepository();
        poDisb = new Disbursement(application);
    }

    public LiveData<List<EDisbursementAttachment>> GetDisbursements(String fsTransNox){
        return poDisb.GetAttachments(fsTransNox);
    }

    public Date FormatToDate(String fsDate){
        return poDate.ConvertStringDate(fsDate, "YYYY-MM-DD");
    }

    public void DownloadDisbursement(String fsTransNox, OnDownload foCallback){

        foCallback.OnLoad();
        poDisb.DownloadDisbursementInfo(fsTransNox).thenAccept(new Consumer<Boolean>() {
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
}
