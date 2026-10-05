package com.gag.masonry.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gag.masonry.BuildConfig;
import com.gag.useraccount.ViewModel.VM_Member;

import org.gag.appdriver.App.Core.Dashboard;
import org.gag.appdriver.App.Core.Lodge;
import org.gag.appdriver.App.Core.UserAccount;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EPosition;
import org.gag.appdriver.Room.Entities.ESysNotifs;
import org.gag.appdriver.Room.Entities.EUserInfo;
import org.gag.appdriver.Room.Entities.EUserObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Main extends AndroidViewModel {

    private final Context loApp;

    private final AppConfig poConfig;
    private final DeviceInfo poDevice;
    private final DateRepository poDate;
    private final Dashboard poDashboard;
    private final UserAccount poAccount;
    private final Lodge poLodge;

    private final MutableLiveData<String> lsFilter = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, ArrayList<String>>> laMemberInfoOthers = new MutableLiveData<>();

    public interface InitData{
        void isLoading();
        void isLoginNeeded();
        void isSessionEnded();
        void hasLoggedIn();
    }

    public interface OnDownloadData{
        void OnDownload();
        void OnFinished(String fsMessage);
    }

    public VM_Main(@NonNull Application application) {
        super(application);

        loApp = application;

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
        poDashboard = new Dashboard(application);
        poAccount = new UserAccount(application);
        poLodge = new Lodge(application);
    }

    public void AddInfoList(HashMap<String, ArrayList<String>> faMemberInfoOthers){
        laMemberInfoOthers.setValue(faMemberInfoOthers);
    }

    public void FilterOfficerHistory(String fsCondition){
        lsFilter.setValue(fsCondition);
    }

    public String GetLodgeId(){
        return poLodge.GetLodgeID();
    }

    public LiveData<List<LodgeCalendarList>> GetLodgeCalendarList(String fsDfrom, String fsDto){
        return poLodge.GetLodgeCalendarList(poLodge.GetLodgeID(), fsDfrom, fsDto);
    }

    public LiveData<EUserInfo> ObserveUserInfo(){
        return poDashboard.getPoDBUser().ObserveUserInfo();
    }

    public LiveData<MemberDashboardInfo> ObserveMemberInfo(){
        return poDashboard.ObserverMemberInfoByUserID();
    }

    public LiveData<String> FilterHistory(){
        return lsFilter;
    }

    public List<OfficerHistory> SearchOfficerHistory(String fsFilter){
        return poDashboard.SearchOfficerHistory(fsFilter);
    }

    public LiveData<List<TownProvince>> ObserveMemberAddress(String fsMemberID){
        return poDashboard.GetMemberAddress(fsMemberID);
    }

    public LiveData<List<EMemberContactInfo>> ObserveMemberContact(String fsMemberID){
        return poDashboard.GetMemberContact(fsMemberID);
    }

    public LiveData<List<EMemberEmailInfo>> ObserveMemberEmail(String fsMemberID) {
        return poDashboard.GetMemberEmail(fsMemberID);
    }

    public LiveData<HashMap<String, ArrayList<String>>> ObserveMemberInfoList(){
        return laMemberInfoOthers;
    }

    public LiveData<OfficerInfo> ObserveCurrentRole(){
        return poAccount.ObserveCurrentRole();
    }

    public LiveData<List<EUserObjects>> GetUserObjects(){
        return poDashboard.GetUserObjects();
    }

    public LiveData<List<ESysNotifs>> GetNotifications(String fsRcvr){
        return poAccount.ObserveUnreadNotifs(fsRcvr);
    }

    public LiveData<List<EPosition>> GetPositions(){
        return poDashboard.GetPositions();
    }

    public String GetCurrentDate(){
        return poDate.GetCurrentDate();
    }

    public String GetFormattedDate(String fsDate, String fsFormat){
        return poDate.FormatDate(fsDate, fsFormat);
    }

    public void InitData(InitData foCallback){

        foCallback.isLoading();

        //if first open, initialze basic config
        if (!poConfig.hasInitialized()){

            poConfig.isInitialize("1");
            poConfig.setProductID("MSNRY_APP");
            poConfig.setProductVersion(String.valueOf(BuildConfig.VERSION_CODE));
            poConfig.setDeviceID(poDevice.GetAndroidID());
        }

        //check session, if login needed (newly opened or empty token)
        if (!poConfig.hasLoggedIn() || poConfig.getokenID().isEmpty()){
            foCallback.isLoginNeeded();
        } else {

            //session expired
            if (!poDate.GetCurrentDate().equalsIgnoreCase(poConfig.getLogDate())) {
                foCallback.isSessionEnded();
                return;
            }

            //download user info
            poAccount.DownloadUserInfo().thenAccept(aBoolean -> {

                if (aBoolean){
                    foCallback.hasLoggedIn();
                }else {
                    foCallback.isLoginNeeded();
                }
            });
        }
    }

    public void DownloadMemberInfo(String fsMemberIDxx, VM_Member.OnDownload foCallback){

        foCallback.Loading();

        //store all threads into hash set, to execute one by one and avoid memory leakage
        HashSet<CompletableFuture<Boolean>> laTasks = new HashSet<>(
                List.of(
                        poAccount.DownloadMemberAddress(fsMemberIDxx),
                        poAccount.DownloadMemberContact(fsMemberIDxx),
                        poAccount.DownloadMemberEmail(fsMemberIDxx),
                        poAccount.DownloadOfficerInfo(fsMemberIDxx)
                )
        );

        //initialize task result holder
        CompletableFuture<Boolean> poResult = CompletableFuture.completedFuture(true);;
        for (CompletableFuture<Boolean> task : laTasks){

            poResult = poResult.thenCompose(aBoolean -> {
                if (!aBoolean) return CompletableFuture.completedFuture(false);
                return task;
            });
        }

        //get the result
        poResult.thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (!aBoolean){
                    foCallback.Finished("Failed to download member information:\n\n" + poAccount.getMessage());
                    return;
                }
                foCallback.Finished("Successfully downloaded member information");
            }
        });
    }

    public void DownloadOfficerHistory(String fsMemberID, String fsYear, OnDownloadData foCallback){

        foCallback.OnDownload();
        poAccount.DownloadOfficerHistory(fsMemberID, fsYear).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poAccount.getMessage());
                }else {
                    foCallback.OnFinished("Successfully downloaded officer history");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.OnFinished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void TagNotifRead(ESysNotifs foNotif, OnDownloadData foCallback){

        foCallback.OnDownload();
        poAccount.ReadUserNotifs(foNotif).thenAccept(aBoolean -> {
            foCallback.OnFinished(!aBoolean ? poAccount.getMessage() : "Successfully tagged");
        });
    }

    public void EndSession(){
        poDashboard.ClearMemberData();
        poConfig.ClearAccountSession();
    }
}
