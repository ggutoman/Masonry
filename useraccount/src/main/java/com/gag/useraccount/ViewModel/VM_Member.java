package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.Constants.MEMBER_STATUS;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;

    public VM_Member(@NonNull Application application) {
        super(application);

        laSponsors = new MutableLiveData<>();
    }

    public void AddSponsor(String fsSponsor){
        List<String> currentList = laSponsors.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(fsSponsor);

        laSponsors.setValue(currentList);
    }

    public LiveData<List<String>> GetSponsorList(){
        return laSponsors;
    }

    public List<String> GetCivilStatus(){

        List<String> laCivil = new ArrayList<>();
        Collections.addAll(laCivil,
                MEMBER_STATUS.URL_STATUS_SINGLE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_MARRIED.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_WIDOWED.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_SEPARATED.getFsDescr()
        );
        return laCivil;
    }

    public List<String> GetAccountStatus(){

        List<String> laAccount = new ArrayList<>();
        Collections.addAll(laAccount,
                MEMBER_STATUS.URL_STATUS_INACTIVE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_ACTIVE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_SUSPENDED.getFsDescr()
        );
        return laAccount;
    }

}
