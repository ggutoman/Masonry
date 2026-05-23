package com.gag.masonry.ViewModel;

import android.app.Application;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.DeviceInfo.DeviceInfo;
import org.gag.appdriver.Libraries.Preferences.AppConfig;

public class VM_Main extends AndroidViewModel {

    private final AppConfig poConfig;
    private final DeviceInfo poDevice;
    private final DateRepository poDate;

    public interface InitData{
        void isLoading();
        void hasLoggedIn();
        void isLoginNeeded();
        void isSessionExpired();
    }

    public VM_Main(@NonNull Application application) {
        super(application);

        poConfig = new AppConfig(application);
        poDevice = new DeviceInfo(application);
        poDate = new DateRepository();
    }

    public void InitData(InitData foCallback){

        //if first open, initialze basic config
        if (!poConfig.hasInitialized()){

            foCallback.isLoading();

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
                foCallback.isSessionExpired();
            }else {
                foCallback.hasLoggedIn();
            }
        }
    }
}
