package com.gag.useraccount.ViewModel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.Constants.DATE_CONSTANTS;
import org.gag.appdriver.Libraries.DateUtil.DateRepository;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.Entities.EUserInfo;

public class VM_Account extends AndroidViewModel {

    private final UserAccount poAccount;
    private final DateRepository poDate;
    private String lsMessage;

    public interface OnLogin{
        void onLoad();
        void onSuccess();
        void onError(String fsError);
    }

    public VM_Account(@NonNull Application application) {
        super(application);

        poAccount = new UserAccount(application);
        poDate = new DateRepository();
    }

    public String GetMessage(){
        return lsMessage;
    }

    public void LoginUser(String fsID, String fsPass, OnLogin foCallback){

        foCallback.onLoad();

        if (fsID.isEmpty()){
            foCallback.onError("Please enter membership ID");
            return;
        }else if (fsPass.isEmpty()){
            foCallback.onError("Please enter password");
            return;
        }

        poAccount.LoginUser(fsID, fsPass)
                .thenAccept(aBoolean -> {

                    if (aBoolean){
                        foCallback.onSuccess();
                    }else{
                        foCallback.onError(poAccount.GetMessage());
                    }

                })
                .exceptionally(throwable -> {
                    foCallback.onError(throwable.getMessage());
                    return null;
                });
    }

    public void CreateUser(EUserInfo poUser, OnLogin foCallback){

        foCallback.onLoad();

        if (poUser.getSUserName().isEmpty()){
            foCallback.onError("Username is not initialized");
            return;
        }else if (poUser.getSPassword().isEmpty()){
            foCallback.onError("Password is not initialized");
            return;
        }else if (poUser.getSGLPIDNoX().isEmpty()){
            foCallback.onError("Member ID is not initialized");
            return;
        }else if (poUser.getSLastName().isEmpty()){
            foCallback.onError("Lastname is not initialized");
            return;
        }else if (poUser.getDBirthDte().isEmpty()){
            foCallback.onError("Birthdate is not initialized");
            return;
        }

        poAccount.CreateUser(poUser)
                .thenAccept(aBoolean -> {

                    if (aBoolean){
                        foCallback.onSuccess();
                    }else{
                        foCallback.onError(poAccount.GetMessage());
                    }

                })
                .exceptionally(throwable -> {
                    foCallback.onError(throwable.getMessage());
                    return null;
                });
    }
}
