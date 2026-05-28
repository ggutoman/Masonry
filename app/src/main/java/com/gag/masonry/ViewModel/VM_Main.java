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
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.Arrays;
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

    public interface OnDownload {
        void OnLoad();
        void OnSuccess();
        void OnError(String fsMEssage);
    }

    public VM_Main(@NonNull Application application) {
        super(application);

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
        poDashboard = new Dashboard(application);
    }

    public LiveData<EUserInfo> GetUserInfo(){
        return poDashboard.getPoDBUser().GetUser();
    }

    public LiveData<EMemberInfo> GetMemberInfo(){
        return poDashboard.ObserverMemberInfoByUserID();
    }

    public ELodgeInfo GetLodgeInfo(){
        return poDashboard.GetLodgeInfo();
    }

    public AppConfig GetSession(){
        return poConfig;
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
                EndSession();
                foCallback.isSessionEnded();
                return;
            }
            foCallback.hasLoggedIn();
        }
    }

    public void DownloadUserData(OnDownload foCallback){;

        CompletableFuture<Boolean> poUserInfo = poDashboard.DownloadUserInfo();
        CompletableFuture<Boolean> poLodgeInfo = poDashboard.DownloadLodgeInfo();

        CompletableFuture.allOf(poUserInfo, poLodgeInfo).thenRun(new Runnable() {
            @Override
            public void run() {

                try{

                    foCallback.OnLoad();

                    if (!poUserInfo.get()){
                        foCallback.OnError("Download User: " + poDashboard.getMessage());
                        return;
                    }else if (!poLodgeInfo.get()){
                        foCallback.OnError("Download Lodge: " + poDashboard.getMessage());
                        return;
                    }
                    foCallback.OnSuccess();
                }catch (Exception e){
                    foCallback.OnError(e.getMessage());
                }
            }
        });
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
