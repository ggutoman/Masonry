package com.gag.accounting.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Core.Projects;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.MemberPledges;
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

    public interface OnTransaction{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMessage);
    }

    public VM_Annual(@NonNull Application application) {
        super(application);

        poAnnual = new Annual(application);
    }

    public String GetCurrentDate(){
        return poAnnual.GetCurrentDate();
    }

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poAnnual.FormatDateString(fsDate, fsFormat);
    }

    public LiveData<List<AnnualSummary>> GetAnnualMemberInfo(){
        return poAnnual.GetAnnualMemberInfo();
    }
    public void DownloadAnnualMembers(OnTransaction foCallback){

        foCallback.OnLoad();
        poAnnual.DownloadAnnualMember("").thenAccept(new Consumer<Boolean>() {
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
