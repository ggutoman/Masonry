package com.gag.masonry.ViewModel;

import android.app.Application;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.gag.appdriver.App.Dashboard.Dashboard;
import org.gag.appdriver.Constants.MENU_ITEM_CONSTANTS;
import org.gag.appdriver.Constants.MENU_PARENT_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.DataObject.DUserInfo;
import org.gag.appdriver.Room.ML_DBF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class VM_Main extends AndroidViewModel {

    private final AppConfig poConfig;
    private final DeviceInfo poDevice;
    private final DateRepository poDate;
    private final Dashboard poDashboard;

    public interface InitData{
        void isLoading();
        void hasLoggedIn(List<MENU_PARENT_CONSTANTS> foParentMenu, HashMap<String, List<MENU_ITEM_CONSTANTS>> foParentItem);
        void isLoginNeeded();
        void isSessionExpired();
    }

    public VM_Main(@NonNull Application application) {
        super(application);

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
        poDashboard = new Dashboard(application);
    }

    public void InitData(InitData foCallback){

        foCallback.isLoading();

        //if first open, initialze basic config
        if (!poConfig.hasInitialized()){

            poConfig.isInitialize("1");
            poConfig.setProductID("MSNRY_APP");
            poConfig.setDeviceID(poDevice.GetAndroidID());
        }

        //check log transaction, if login needed
        if (!poConfig.hasLoggedIn()){
            foCallback.isLoginNeeded();
        } else {

            poConfig.getokenID();
            if (poConfig.getokenID().isEmpty()){
                foCallback.isLoginNeeded();
            }else if (!poDate.GetCurrentDate().equalsIgnoreCase(poConfig.getLogDate())) {

                poConfig.ClearAccountSession();

                foCallback.isSessionExpired();
            }else {

                DUserInfo poUserDb = poDashboard.GetUserInfo().GetUserDao();
                List<MENU_PARENT_CONSTANTS> GetParentMenu = poDashboard.GetParentMenus(poUserDb.GetUser().getNUserLevl());

                HashMap<String, List<MENU_ITEM_CONSTANTS>> GetParentItem = new HashMap<>();
                for (MENU_PARENT_CONSTANTS entries : GetParentMenu){

                    GetParentItem.put(entries.getFsIDxx(), poDashboard.GetParentItems(poUserDb.GetUser().getNUserLevl(), entries.getFsIDxx()));
                }

                foCallback.hasLoggedIn(GetParentMenu, GetParentItem);
            }
        }
    }

    public void DownloadUserInfo(String fsID){

        poDashboard.DownloadUserInfo(fsID).thenAccept(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {

                if (!aBoolean){
                    
                }
            }
        });
    }
}
