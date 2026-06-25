package com.gag.masonry.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gag.useraccount.ViewModel.VM_Member;

import org.gag.appdriver.App.Core.Dashboard;
import org.gag.appdriver.App.Core.UserAccount;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EOfficerHistory;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Main extends AndroidViewModel {

    private final AppConfig poConfig;
    private final DeviceInfo poDevice;
    private final DateRepository poDate;
    private final Dashboard poDashboard;
    private final UserAccount poAccount;

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

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
        poDashboard = new Dashboard(application);
        poAccount = new UserAccount(application);
    }

    public void AddInfoList(HashMap<String, ArrayList<String>> faMemberInfoOthers){
        laMemberInfoOthers.setValue(faMemberInfoOthers);
    }

    public void FilterOfficerHistory(String fsCondition){
        lsFilter.setValue(fsCondition);
    }

    public EUserInfo GetUserInfo(){
        return poDashboard.getPoDBUser().GetUserInfo();
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

    public LiveData<List<EMemberInfo>> GetMemberList(String fsMemberIDx, String fsDfrom, String fsDto) {
        return poDashboard.ObserveMemberList(fsMemberIDx, fsDfrom, fsDto);
    }

    public LiveData<List<TownProvince>> ObserveMemberAddress(String fsMemberID){
        return poDashboard.GetMemberAddress(fsMemberID);
    }

    public LiveData<List<EMemberContactInfo>> ObserveMemberContact(String fsMemberID){
        return poDashboard.GetMemberContact(fsMemberID);
    }

    public LiveData<List<EMemberEmailInfo>> ObserveMemberEmail(String fsMemberID){
        return poDashboard.GetMemberEmail(fsMemberID);
    }

    public LiveData<List<OfficerInfo>> ObserveOfficerList(String fsMemberIDx, String fsDfrom, String fsDto) {
        return poDashboard.ObserveOfficersList(fsMemberIDx, fsDfrom, fsDto);
    }

    public LiveData<HashMap<String, ArrayList<String>>> ObserveMemberInfoList(){
        return laMemberInfoOthers;
    }

    public LiveData<OfficerInfo> ObserveCurrentRole(String fsMemberID){
        return poAccount.ObserveCurrentRole(fsMemberID);
    }

    public List<MENU_PARENT_CONSTANTS> GetParentMenu(int fnUserLvl){
        return poDashboard.GetParentMenus(fnUserLvl);
    }

    public List<MENU_ITEM_CONSTANTS> GetMenuItem(int fnUserLvl, String fsParentIDx){
        return poDashboard.GetParentItems(fnUserLvl, fsParentIDx);
    }

    public ELodgeInfo GetLodgeInfo(){
        return poDashboard.GetLodgeInfo();
    }

    public String GetCurrentDate(){
        return poDate.GetCurrentDate();
    }

    public String GetFirstQuarter(){
        return poDate.GetCountedDate(4, 0, false);
    }

    public String GetFormattedDate(Long flDate){
        return poDate.FormatLongDate(flDate);
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
            poConfig.setDeviceID(poDevice.GetAndroidID());
        }

        //check session, if login needed (newly opened or empty token)
        if (!poConfig.hasLoggedIn() || poConfig.getokenID().isEmpty()){
            foCallback.isLoginNeeded();
        } else {

            if (!poDate.GetCurrentDate().equalsIgnoreCase(poConfig.getLogDate())) {
                foCallback.isSessionEnded();
                return;
            }

            //store all threads into hash set, to execute one by one and avoid memory leakage
            HashSet<CompletableFuture<Boolean>> laTasks = new HashSet<>(
                    List.of(
                            poDashboard.DownloadUserInfo(),
                            poDashboard.DownloadLodgeInfo(),
                            poDashboard.DownloadPositionInfo(),
                            poDashboard.DownloadTitleInfo(),
                            poDashboard.DownloadProvinceInfo(),
                            poDashboard.DownloadTownInfo(),
                            poDashboard.DownloadLodgeCalendar(),
                            poDashboard.DownloadMemberList(GetFirstQuarter(), GetCurrentDate()),
                            poDashboard.DownloadOfficerList(GetFirstQuarter(), GetCurrentDate())
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
                        Log.d("Download Data: ", poDashboard.getMessage());
                    }
                    foCallback.hasLoggedIn();
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

    public void DownloadParameters(OnDownloadData foCallback){

        foCallback.OnDownload();

        //store all threads into hash set, to execute one by one and avoid memory leakage
        HashSet<CompletableFuture<Boolean>> laTasks = new HashSet<>(
                List.of(
                        poDashboard.DownloadUserInfo(),
                        poDashboard.DownloadLodgeInfo(),
                        poDashboard.DownloadPositionInfo(),
                        poDashboard.DownloadTitleInfo(),
                        poDashboard.DownloadProvinceInfo(),
                        poDashboard.DownloadTownInfo(),
                        poDashboard.DownloadLodgeCalendar()
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
                    foCallback.OnFinished(poDashboard.getMessage());
                }
                foCallback.OnFinished("Successfully downloaded data");
            }
        });
    }

    public void DownloadMembers(String fdFrom, String fDto, OnDownloadData foCallback){

        foCallback.OnDownload();
        poDashboard.DownloadMemberList(fdFrom, fDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDashboard.getMessage());
                }else {
                    foCallback.OnFinished("Successfully downloaded member list");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.OnFinished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void DownloadOfficers(String fdFrom, String fDto, OnDownloadData foCallback){

        foCallback.OnDownload();
        poDashboard.DownloadOfficerList(fdFrom, fDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDashboard.getMessage());
                }else {
                    foCallback.OnFinished("Successfully downloaded officer list");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.OnFinished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void DownloadOfficerHistory(String fsMemberID, String fdFrom, String fDto, OnDownloadData foCallback){

        foCallback.OnDownload();
        poDashboard.DownloadOfficerHistory(fsMemberID, fdFrom, fDto).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    foCallback.OnFinished(poDashboard.getMessage());
                }else {
                    foCallback.OnFinished("Successfully downloaded officer history");
                }
            }
        }).exceptionally(throwable -> {
            foCallback.OnFinished("Could not make request at this moment:\n\n" + throwable.getMessage());
            return null;
        });
    }

    public void EndSession(){
        poDashboard.ClearMemberData();
        poConfig.ClearAccountSession();
        poDashboard.getPoDBUser().DeleteUser();
    }
}
