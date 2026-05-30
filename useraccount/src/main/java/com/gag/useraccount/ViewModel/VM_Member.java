package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.Constants.MEMBER_STATUS;
import org.gag.appdriver.Room.DataObject.DTownInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.ETitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;
    private final MutableLiveData<String> lsTownSearch;
    private final MutableLiveData<List<DTownInfo.TownProvince>> laAddress;
    private final MutableLiveData<List<EMemberContactInfo>> laContact;
    private final MutableLiveData<List<EMemberEmailInfo>> laEmail;

    private final UserAccount poAccount;

    public VM_Member(@NonNull Application application) {
        super(application);

        laSponsors = new MutableLiveData<>();
        lsTownSearch = new MutableLiveData<>();
        laAddress = new MutableLiveData<>();
        laContact = new MutableLiveData<>();
        laEmail = new MutableLiveData<>();

        poAccount = new UserAccount(application);
    }

    public void SearchTownProvince(String fsTown){
        lsTownSearch.postValue(fsTown);
    }

    public boolean AddSponsor(String fsSponsor){
        List<String> currentList = laSponsors.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        if (currentList.size() >= 3){
            return false;
        }
        currentList.add(fsSponsor);
        laSponsors.setValue(currentList);
        return true;
    }

    public void AddMemberAddress(String fsAddressIDx, String fsTownIDx, String fsProvIDx, String fsProvNme, String fsAddressx, String isHomeAddrssx, String isActive){

        DTownInfo.TownProvince loAddress = new DTownInfo.TownProvince(
                fsAddressIDx,
                fsTownIDx,
                fsProvIDx,
                fsProvNme,
                fsAddressx,
                isHomeAddrssx,
                isActive
        );

        List<DTownInfo.TownProvince> currentList = laAddress.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loAddress);
        laAddress.setValue(currentList);
    }

    public void AddMemberContact(String fsContactID, String fsMemberID, String fsContactNo, String fsRemarks, String fsStatus){

        EMemberContactInfo loContact = new EMemberContactInfo(
                fsContactID,
                fsMemberID,
                fsContactNo,
                fsRemarks,
                fsStatus,
                poAccount.GetUserID(),
                poAccount.GetCurrentDate(),
                poAccount.GetCurrentDate()
        );

        List<EMemberContactInfo> currentList = laContact.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loContact);
        laContact.setValue(currentList);
    }

    public void AddMemberEmail(String fsEmailID, String fsMemberID, String fsEmailAdd, String fsStatus){

        EMemberEmailInfo loContact = new EMemberEmailInfo(
                fsEmailID,
                fsMemberID,
                fsEmailAdd,
                fsStatus,
                poAccount.GetUserID(),
                poAccount.GetCurrentDate(),
                poAccount.GetCurrentDate()
        );

        List<EMemberEmailInfo> currentList = laEmail.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loContact);
        laEmail.setValue(currentList);
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

    public LiveData<List<DTownInfo.TownProvince>> SearchTown(String fsSearch){
        return poAccount.SearchTown(fsSearch);
    }
    public LiveData<List<DTownInfo.TownProvince>> HasNewAddress(){
        return laAddress;
    }

    public LiveData<List<EMemberContactInfo>> HasNewContact(){
        return laContact;
    }

    public LiveData<List<EMemberEmailInfo>> HasNewEmail(){
        return laEmail;
    }

    public List<DTownInfo.TownProvince> GetMemberAddress(String fsMemberID){
        return poAccount.GetMemberAddress(fsMemberID);
    }

    public List<EMemberContactInfo> GetMemberContact(String fsMemberID){
        return poAccount.GetMemberContact(fsMemberID);
    }

    public List<EMemberEmailInfo> GetMemberEmail(String fsMemberID){
        return poAccount.GetMemberEmail(fsMemberID);
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

    public LiveData<EMemberInfo> GetMemberGLPID(String fsGLPIDxx){
        return poAccount.GetMemberGLPID(fsGLPIDxx);
    }

}
