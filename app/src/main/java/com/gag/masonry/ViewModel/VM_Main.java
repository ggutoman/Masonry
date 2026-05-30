package com.gag.masonry.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Dashboard.Dashboard;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public VM_Main(@NonNull Application application) {
        super(application);

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
        poDashboard = new Dashboard(application);
    }

    public LiveData<EUserInfo> GetUserInfo(){
        return poDashboard.getPoDBUser().ObserveUserInfo();
    }

    public LiveData<DMemberInfo.MemberDashboardInfo> GetMemberInfo(){
        return poDashboard.ObserverMemberInfoByUserID();
    }

    public ELodgeInfo GetLodgeInfo(){
        return poDashboard.GetLodgeInfo();
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

            CompletableFuture<Boolean> poUserInfo = poDashboard.DownloadUserInfo();
            CompletableFuture<Boolean> poLodgeInfo = poDashboard.DownloadLodgeInfo();
            CompletableFuture<Boolean> poPosition = poDashboard.DownloadPositionInfo();
            CompletableFuture<Boolean> poTitle = poDashboard.DownloadTitleInfo();
            CompletableFuture<Boolean> poProvince = poDashboard.DownloadProvinceInfo();
            CompletableFuture<Boolean> poTown = poDashboard.DownloadTownInfo();

            CompletableFuture.allOf(poUserInfo, poLodgeInfo).thenRun(new Runnable() {
                @Override
                public void run() {

                    try{

                        if (!poUserInfo.get()){
                            Log.d("Download User:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }else if (!poLodgeInfo.get()){
                            Log.d("Download Lodge:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }else if (!poPosition.get()){
                            Log.d("Download Position:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }else if (!poTitle.get()){
                            Log.d("Download Title:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }else if (!poProvince.get()){
                            Log.d("Download Province:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }else if (!poTown.get()){
                            Log.d("Download Town:", poDashboard.getMessage());
                            foCallback.isLoginNeeded();
                            return;
                        }
                        foCallback.hasLoggedIn();
                    }catch (Exception e){
                        Log.d("Download Information:", poDashboard.getMessage());
                        foCallback.isLoginNeeded();
                    }
                }
            });
        }
    }

    public void EndSession(){
        poDashboard.getPoDBUser().DeleteUser();
        poDashboard.getPoDBMember().DeleteMember();
        poConfig.ClearAccountSession();
    }

    public List<MENU_PARENT_CONSTANTS> GetParentMenu(int fnUserLvl){
        return poDashboard.GetParentMenus(fnUserLvl);
    }

    public List<MENU_ITEM_CONSTANTS> GetMenuItem(int fnUserLvl, String fsParentIDx){
        return poDashboard.GetParentItems(fnUserLvl, fsParentIDx);
    }
}
