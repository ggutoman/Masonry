package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.App.Dashboard.Dashboard;
import org.gag.appdriver.Constants.MEMBER_STATUS;
import org.gag.appdriver.Room.DataObject.DTownInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.ETitle;
import org.gag.appdriver.Room.Entities.ETownCity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;
    private final MutableLiveData<String> lsTownSearch;
    private final UserAccount poAccount;
    private final Dashboard poDashboad;

    public VM_Member(@NonNull Application application) {
        super(application);

        laSponsors = new MutableLiveData<>();
        lsTownSearch = new MutableLiveData<>();
        poAccount = new UserAccount(application);
        poDashboad = new Dashboard(application);
    }

    public void AddSponsor(String fsSponsor){
        List<String> currentList = laSponsors.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(fsSponsor);

        laSponsors.setValue(currentList);
    }

    public void SearchTownProvince(String fsTown){
        lsTownSearch.postValue(fsTown);
    }

    public LiveData<List<String>> GetSponsorList(){
        return laSponsors;
    }

    public LiveData<String> TownSearch(){
        return lsTownSearch;
    }

    public LiveData<List<ELodgeInfo>> GetLodgeList() {
        return poAccount.GetLodges();
    }

    public LiveData<List<ETitle>> GetTitleList(){
        return poAccount.GetTitleList();
    }

    public LiveData<List<DTownInfo.TownProvince>> GetTownList(String fsProvIDx){
        return poDashboad.ObserveTownInfo(fsProvIDx);
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

    public String GenerateGLPID(){
        return poAccount.GenerateGLPID();
    }

}
