package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.gag.appdriver.App.Accounts.UserAccount;

public class VM_Account extends AndroidViewModel {

    private final UserAccount poAccount;

    public interface OnLogin{
        void onSuccess();
        void onError(String fsError);
    }

    public VM_Account(@NonNull Application application) {
        super(application);

        poAccount = new UserAccount(application);
    }

    public void LoginUser(String fsID, String fsPass, OnLogin foCallback){

        poAccount.LoginUser(fsID, fsPass)
                .thenAccept(aBoolean -> {

                    if (aBoolean){
                        foCallback.onSuccess();
                    }else{
                        foCallback.onError(poAccount.GetMessage());
                    }

                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }
}
