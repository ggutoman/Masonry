package com.gag.useraccount.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.App.Member.Member;
import org.gag.appdriver.Repository.RLodge;
import org.gag.appdriver.Repository.RTitle;
import org.gag.appdriver.Room.Entities.ELodge;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContact;
import org.gag.appdriver.Room.Entities.EMemberEmail;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EMemberMaster;
import org.gag.appdriver.Room.Entities.ETitle;
import org.gag.appdriver.Room.Entities.EUserInfo;

import java.util.List;

public class VM_Member extends AndroidViewModel {

    private final Member poMember;
    private final RTitle poTitle;
    private final RLodge poLodge;
    public LiveData<List<ETitle>> GetTitles(){
        return poTitle.GetTitles();
    }
    public LiveData<List<ELodge>> GetLodges(){
        return poLodge.GetLodges();
    }

    public interface OnLogin{
        void onLoad();
        void onSuccess();
        void onError(String fsError);
    }

    public VM_Member(@NonNull Application application) {
        super(application);

        poMember = new Member(application);
        poTitle = new RTitle(application);
        poLodge = new RLodge(application);
    }

    public void CreateMember(
            EMemberMaster poMemberMaster,
            List<EMemberAddress> poMemberAddress,
            List<EMemberContact> poMemberContact,
            List<EMemberEmail> poMemberEmail,
            OnLogin foCallback) {

        foCallback.onLoad();

        /**
         * VALIDATIONS
         */
        if (poMemberMaster == null) {

            foCallback.onError("Member information is not initialized");
            return;
        }

//        if (poMemberMaster.getSMemberID() == null ||
//                poMemberMaster.getSMemberID().trim().isEmpty()) {
//
//            foCallback.onError("Member ID is not initialized");
//            return;
//        }

        if (poMemberMaster.getSLastName() == null ||
                poMemberMaster.getSLastName().trim().isEmpty()) {

            foCallback.onError("Lastname is not initialized");
            return;
        }

        if (poMemberMaster.getSFrstName() == null ||
                poMemberMaster.getSFrstName().trim().isEmpty()) {

            foCallback.onError("Firstname is not initialized");
            return;
        }

//        if (poMemberMaster.getDBirthDte() == null ||
//                poMemberMaster.getDBirthDte().trim().isEmpty()) {
//
//            foCallback.onError("Birthdate is not initialized");
//            return;
//        }

        /**
         * ADDRESS VALIDATION
         */
        if (poMemberAddress == null || poMemberAddress.isEmpty()) {

            foCallback.onError("Member address is not initialized");
            return;
        }

        /**
         * CONTACT VALIDATION
         */
        if (poMemberContact == null || poMemberContact.isEmpty()) {

            foCallback.onError("Member contact is not initialized");
            return;
        }

        /**
         * EMAIL VALIDATION
         */
        if (poMemberEmail == null || poMemberEmail.isEmpty()) {

            foCallback.onError("Member email is not initialized");
            return;
        }

        /**
         * CREATE MEMBER
         */
        poMember.CreateMember(
                poMemberMaster,
                poMemberAddress,
                poMemberContact,
                poMemberEmail
        ).thenAccept(aBoolean -> {

            if (aBoolean) {

                foCallback.onSuccess();

            } else {

                foCallback.onError(poMember.GetMessage());
            }

        }).exceptionally(throwable -> {

            foCallback.onError(
                    throwable.getMessage() != null
                            ? throwable.getMessage()
                            : "Unable to process request"
            );

            return null;
        });
    }

}
