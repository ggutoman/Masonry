package com.gag.useraccount.ViewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Core.UserAccount;
import org.gag.appdriver.Libraries.Encryption.HashRepository;
import org.gag.appdriver.Libraries.Preferences.AppConfig;
import org.gag.appdriver.Room.Entities.EUserInfo;

public class VM_Account extends AndroidViewModel {

    private final UserAccount poAccount;

    public interface OnSubmit {
        void onLoad();
        void onSuccess();
        void onError(String fsError);
    }

    public VM_Account(@NonNull Application application) {
        super(application);

        poAccount = new UserAccount(application);
    }

    public AppConfig GetSession(){
        return poAccount.GetSession();
    }

    public HashRepository GetEncryption(){
        return poAccount.GetEncryption();
    }

    public LiveData<EUserInfo> GetUserInfo(){
        return poAccount.GetUserInfo();
    }

    public void LoginUser(String fsID, String fsPass, OnSubmit foCallback){

        foCallback.onLoad();

        if (fsID.isEmpty()){
            foCallback.onError("Please enter username");
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
                    foCallback.onError("Could not make request at this moment:\n\n" + throwable.getMessage());
                    return null;
                });
    }

    public void CreateUser(EUserInfo poUser, OnSubmit foCallback){

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
                    foCallback.onError("Could not make request at this moment:\n\n" + throwable.getMessage());
                    return null;
                });
    }

    public void UpdateUser(EUserInfo poUser, OnSubmit foCallback){

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

        poAccount.UpdateUser(poUser)
                .thenAccept(aBoolean -> {

                    if (aBoolean){
                        foCallback.onSuccess();
                    }else{
                        foCallback.onError(poAccount.GetMessage());
                    }

                })
                .exceptionally(throwable -> {
                    foCallback.onError("Could not make request at this moment:\n\n" + throwable.getMessage());
                    return null;
                });

    }
}
