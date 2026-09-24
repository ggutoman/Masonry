package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Core.UserAccount;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Constants.MEMBER_CONSTANTS;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EOfficer;
import org.gag.appdriver.Room.Entities.EPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;
    private final MutableLiveData<List<TownProvince>> laAddress;
    private final MutableLiveData<List<EMemberContactInfo>> laContact;
    private final MutableLiveData<List<EMemberEmailInfo>> laEmail;

    private final UserAccount poAccount;

    public interface OnSubmit{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMesssage);
    }

    public interface OnDownload{
        void Loading();
        void Finished(String fsMessage);
    }

    public interface OnGetALSMID {
        void onLoad();
        void onSuccess(String fsID);
        void onError(String fsError);
    }

    public VM_Member(@NonNull Application application) {
        super(application);

        laSponsors = new MutableLiveData<>();
        laAddress = new MutableLiveData<>();
        laContact = new MutableLiveData<>();
        laEmail = new MutableLiveData<>();

        poAccount = new UserAccount(application);
    }

    public void ClearSponsor(){
        laSponsors.setValue(new ArrayList<>());
    }

    public void AddMemberAddress(String fsAddressIDx, String fsTownIDx, String fsProvIDx, String fsProvNme, String fsAddressx, String isHomeAddrssx, String isActive){

        TownProvince loAddress = new TownProvince(
                fsAddressIDx,
                fsTownIDx,
                fsProvIDx,
                fsProvNme,
                fsAddressx,
                isHomeAddrssx,
                isActive
        );

        List<TownProvince> currentList = laAddress.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loAddress);
        laAddress.setValue(currentList);
    }

    public void ReplaceAddress(int index, TownProvince loAddress){

        List<TownProvince> currentList = laAddress.getValue();

        if (currentList == null) return;

        currentList.set(index, loAddress);
        laAddress.setValue(currentList);
    }

    public void ClearAddress(){
        laAddress.setValue(new ArrayList<>());
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

    public void ReplaceContact(int index, String fsRemarks, String fsStatus){

        List<EMemberContactInfo> currentList = laContact.getValue();

        if (currentList == null) return;

        EMemberContactInfo loContact = laContact.getValue().get(index);
        loContact.setSRemarksx(fsRemarks);
        loContact.setCRecdStat(fsStatus);

        currentList.set(index, loContact);
        laContact.setValue(currentList);
    }

    public void ClearContacts(){
        laContact.setValue(new ArrayList<>());
    }

    public void AddMemberEmail(String fsEmailID, String fsMemberID, String fsEmailAdd, String fsStatus){

        EMemberEmailInfo loEmail = new EMemberEmailInfo(
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
        currentList.add(loEmail);
        laEmail.setValue(currentList);
    }

    public void ReplaceEmail(int index, String fsStatus){

        List<EMemberEmailInfo> currentList = laEmail.getValue();

        if (currentList == null) return;

        EMemberEmailInfo loEmail = laEmail.getValue().get(index);
        loEmail.setCRecdStat(fsStatus);

        currentList.set(index, loEmail);
        laEmail.setValue(currentList);
    }

    public void ClearEmails(){
        laEmail.setValue(new ArrayList<>());
    }

    public String GetCurrentDate(){
        return poAccount.GetCurrentDate();
    }

    public String GetCurrentDateTime(){
        return poAccount.GetCurrentDateTime();
    }

    public String GetFormattedDate(Long flDate){
        return poAccount.FormatLongDate(flDate);
    }

    public String GetFormattedDate(String fsInput, String fsFormat){
        return poAccount.GetFormattedDate(fsInput, fsFormat);
    }

    public String GetUserID(){
        return poAccount.GetUserID();
    }

    public String GetLodgeID(){
        return poAccount.GetLodgeID();
    }

    public LiveData<List<EMemberInfo>> GetMemberList(String fsMemberID, String fsDfrom, String fsDto) {
        return poAccount.ObserveMemberList(fsMemberID, fsDfrom, fsDto);
    }

    public LiveData<List<OfficerInfo>> ObserveOfficerList(String fsMemberIDx, String fsYear) {
        return poAccount.ObserveOfficersList(fsMemberIDx, fsYear);
    }

    public LiveData<List<EMemberInfo>> ObserverMemberList(){
        return poAccount.ObserveMemberList();
    }

    public LiveData<List<TownProvince>> SearchTown(String fsSearch){
        return poAccount.SearchTown(fsSearch);
    }

    public LiveData<List<TownProvince>> HasNewAddress(){
        return laAddress;
    }

    public LiveData<List<EMemberContactInfo>> HasNewContact(){
        return laContact;
    }

    public LiveData<List<EMemberEmailInfo>> HasNewEmail(){
        return laEmail;
    }

    public LiveData<EMemberInfo> ObserveMemberInfo(String fsMemberID){
        return poAccount.ObserveMemberInfo(fsMemberID);
    }

    public LiveData<List<TownProvince>> GetMemberAddress(String fsMemberID){
        return poAccount.GetMemberAddress(fsMemberID);
    }

    public LiveData<List<EMemberContactInfo>> GetMemberContact(String fsMemberID){
        return poAccount.GetMemberContact(fsMemberID);
    }

    public LiveData<List<EMemberEmailInfo>> GetMemberEmail(String fsMemberID){
        return poAccount.GetMemberEmail(fsMemberID);
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendar(){
        return poAccount.ObserveLodgeCalendarList();
    }

    public LiveData<List<EPosition>> ObserverPositionList(){
        return poAccount.ObserverPositionList();
    }

    public LiveData<EOfficer> ObserveOfficerInfo(String fsMemberIDxx, String fsYearIDxx){
        return poAccount.ObserveOfficerInfo(fsMemberIDxx, fsYearIDxx);
    }

    public List<String> GetCivilStatus(){

        List<String> laCivil = new ArrayList<>();
        Collections.addAll(laCivil,
                MEMBER_CONSTANTS.STATUS_SINGLE.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_MARRIED.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_WIDOWED.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_SEPARATED.getFsDescr()
        );
        return laCivil;
    }

    public List<String> GetAccountStatus(){

        List<String> laAccount = new ArrayList<>();
        Collections.addAll(laAccount,
                MEMBER_CONSTANTS.STATUS_REGULAR_MEMBER.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_LIFE_MEMBER.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_DUAL_MEMBER.getFsDescr(),
                MEMBER_CONSTANTS.STATUS_DECEASED_MEMBER.getFsDescr()
        );
        return laAccount;
    }

    public void DownloadMembers(String fsDfromxx, String fsDToxx, OnDownload foCallback){

        foCallback.Loading();
        poAccount.DownloadMemberList(fsDfromxx, fsDToxx).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.Finished(poAccount.getMessage());
                }else {
                    foCallback.Finished("Successfully downloaded member list");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.Finished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void DownloadOfficers(String fsYear, OnDownload foCallback){

        foCallback.Loading();
        poAccount.DownloadOfficerList(fsYear).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.Finished(poAccount.getMessage());
                }else {
                    foCallback.Finished("Successfully downloaded officer list");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.Finished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void SubmitParameters(EMemberInfo memberInfo, List<EMemberAddress> laAddress, List<EMemberContactInfo> laContact, List<EMemberEmailInfo> laEmail, OnSubmit foCallback){

        foCallback.OnLoad();

        //run first background to get the result
        poAccount.SaveMember(memberInfo)
                .thenCompose(result -> {

                    //if return is booleen
                    if (result instanceof Boolean && !(Boolean) result) {
                        foCallback.OnFailed(poAccount.getMessage());
                        return CompletableFuture.completedFuture(false);
                    }

                    //if return is empty string
                    if (result instanceof String && (((String) result).isEmpty())) {
                        foCallback.OnFailed("Could not save member address. Member ID not found");
                        return CompletableFuture.completedFuture(false);
                    }
                    String lsMemberID = (String) result;

                    //initialize result holder, return true as default
                    CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);

                    // Save addresses one by one
                    for (EMemberAddress loAddress : new HashSet<>(laAddress)) {
                        loAddress.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.SaveMemberAddress(loAddress);
                        });
                    }

                    // Save contacts one by one
                    for (EMemberContactInfo loContact : new HashSet<>(laContact)) {
                        loContact.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.SaveMemberContact(loContact);
                        });
                    }

                    // Save emails one by one, with the generated member id from result
                    for (EMemberEmailInfo loEmail : new HashSet<>(laEmail)) {
                        loEmail.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.SaveMemberEmail(loEmail);
                        });
                    }

                    return chain;

                })
                .thenAccept(allOk -> {
                    foCallback.OnLoad();
                    if (allOk) {
                        foCallback.OnSuccess();
                    } else {
                        foCallback.OnFailed(poAccount.getMessage());
                    }
                })
                .exceptionally(e -> {
                    foCallback.OnFailed("Could not make request at this moment:\n\n" + e.getMessage());
                    return null;
                });

    }

    public void SendSms(String fsContact, String fsMessage, OnSubmit foCallback){

        foCallback.OnLoad();
        poAccount.SendSMS(fsContact, fsMessage).thenAccept(aBoolean -> {

            if (!aBoolean){
                foCallback.OnFailed(poAccount.getMessage());
                return;
            }
            foCallback.OnSuccess();
        });
    }

    public void AssignOfficer(EOfficer foOfficer, String fsYear, String fsRemarksx, VM_Account.OnSubmit foCallback){

        foCallback.onLoad();

        poAccount.SaveOfficer(foOfficer, fsRemarksx).thenCompose(aBoolean -> {

            CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(aBoolean);
            if (aBoolean){
                chain = poAccount.DownloadOfficerList(fsYear);
            }
            return chain;

        }).thenAccept(o -> {

            if (!o){
                foCallback.onError(poAccount.getMessage());
            }else {
                foCallback.onSuccess();
            }
        }).exceptionally(new Function<Throwable, Void>() {
            @Override
            public Void apply(Throwable throwable) {
                foCallback.onError(throwable.getMessage());
                return null;
            }
        });
    }

    public void GenerateALSMID(String fsIDxx, OnGetALSMID foCallback){

        foCallback.onLoad();
        poAccount.GenerateALSMID(fsIDxx).thenAccept(o -> {

            if (o instanceof Boolean){

                if (!(Boolean) o){
                    foCallback.onError(poAccount.getMessage());
                }else {
                    foCallback.onError("Failed to generate ALSM ID");
                }
            }else if (o instanceof String){

                if (((String) o).isEmpty()){
                    foCallback.onError("Failed to generate ALSM ID");
                }else {
                    foCallback.onSuccess((String) o);
                }
            }else{
                foCallback.onError("Failed to generate ALSM ID");
            }
        });
    }
}
