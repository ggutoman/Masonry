package com.gag.masonry.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.Dashboard;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.DataObject.DOfficer;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Main extends AndroidViewModel {

    private final AppConfig poConfig;
    private final DeviceInfo poDevice;
    private final DateRepository poDate;
    private final Dashboard poDashboard;

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
    }

    public EUserInfo GetUserInfo(){
        return poDashboard.getPoDBUser().GetUserInfo();
    }

    public LiveData<EUserInfo> ObserveUserInfo(){
        return poDashboard.getPoDBUser().ObserveUserInfo();
    }

    public DMemberInfo.MemberDashboardInfo GetMemberInfo(String fsUserIDxx){
        return poDashboard.getPoDBMember().GetMemberParameters(fsUserIDxx);
    }

    public LiveData<DMemberInfo.MemberDashboardInfo> ObserveMemberInfo(){
        return poDashboard.ObserverMemberInfoByUserID();
    }

    public LiveData<List<EMemberInfo>> GetMemberList(String fsMemberIDx, String fsDfrom, String fsDto) {
        return poDashboard.ObserveMemberList(fsMemberIDx, fsDfrom, fsDto);
    }

    public LiveData<List<DOfficer.OfficerList>> ObserveOfficerList(String fsMemberIDx, String fsDfrom, String fsDto) {
        return poDashboard.ObserveOfficersList(fsMemberIDx, fsDfrom, fsDto);
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

    public void EndSession(){
        poDashboard.ClearMemberData();
        poConfig.ClearAccountSession();
        poDashboard.getPoDBUser().DeleteUser();
    }
}
