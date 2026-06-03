package com.gag.useraccount.ViewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.gag.appdriver.App.Accounts.UserAccount;
import org.gag.appdriver.Constants.MEMBER_STATUS;
import org.gag.appdriver.Room.DataObject.DTownInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.ETitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;
    private final MutableLiveData<List<DTownInfo.TownProvince>> laAddress;
    private final MutableLiveData<List<EMemberContactInfo>> laContact;
    private final MutableLiveData<List<EMemberEmailInfo>> laEmail;

    private final UserAccount poAccount;
    public interface OnSubmit{
        void OnLoad();
        void OnSuccess();
        void OnFailed(String fsMesssage);
    }

    public VM_Member(@NonNull Application application) {
        super(application);

        laSponsors = new MutableLiveData<>();
        laAddress = new MutableLiveData<>();
        laContact = new MutableLiveData<>();
        laEmail = new MutableLiveData<>();

        poAccount = new UserAccount(application);
    }

    public boolean AddSponsor(String fsSponsor){
        List<String> currentList = laSponsors.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        if (currentList.size() >= 3){
            return false;
        }
        currentList.add(fsSponsor);
        laSponsors.setValue(currentList);
        return true;
    }

    public void ReplaceSponsor(int index, String fsSponsor){

        List<String> currentList = laSponsors.getValue();

        if (currentList == null) return;

        currentList.set(index, fsSponsor);
        laSponsors.setValue(currentList);
    }

    public void AddMemberAddress(String fsAddressIDx, String fsTownIDx, String fsProvIDx, String fsProvNme, String fsAddressx, String isHomeAddrssx, String isActive){

        DTownInfo.TownProvince loAddress = new DTownInfo.TownProvince(
                fsAddressIDx,
                fsTownIDx,
                fsProvIDx,
                fsProvNme,
                fsAddressx,
                isHomeAddrssx,
                isActive
        );

        List<DTownInfo.TownProvince> currentList = laAddress.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loAddress);
        laAddress.setValue(currentList);
    }

    public void ReplaceAddress(int index, DTownInfo.TownProvince loAddress){

        List<DTownInfo.TownProvince> currentList = laAddress.getValue();

        if (currentList == null) return;

        currentList.set(index, loAddress);
        laAddress.setValue(currentList);
    }

    public void AddMemberContact(String fsContactID, String fsMemberID, String fsContactNo, String fsRemarks, String fsStatus){

        EMemberContactInfo loContact = new EMemberContactInfo(
                fsContactID,
                fsMemberID,
                fsContactNo,
                fsRemarks,
                fsStatus,
                poAccount.GetUserID(),
                poAccount.GetCurrentDate(),
                poAccount.GetCurrentDate()
        );

        List<EMemberContactInfo> currentList = laContact.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loContact);
        laContact.setValue(currentList);
    }

    public void ReplaceContact(int index, String fsRemarks, String fsStatus){

        List<EMemberContactInfo> currentList = laContact.getValue();

        if (currentList == null) return;

        EMemberContactInfo loContact = laContact.getValue().get(index);
        loContact.setSRemarksx(fsRemarks);
        loContact.setCRecdStat(fsStatus);

        currentList.set(index, loContact);
        laContact.setValue(currentList);
    }

    public void AddMemberEmail(String fsEmailID, String fsMemberID, String fsEmailAdd, String fsStatus){

        EMemberEmailInfo loEmail = new EMemberEmailInfo(
                fsEmailID,
                fsMemberID,
                fsEmailAdd,
                fsStatus,
                poAccount.GetUserID(),
                poAccount.GetCurrentDate(),
                poAccount.GetCurrentDate()
        );

        List<EMemberEmailInfo> currentList = laEmail.getValue();

        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        currentList.add(loEmail);
        laEmail.setValue(currentList);
    }

    public void ReplaceEmail(int index, String fsStatus){

        List<EMemberEmailInfo> currentList = laEmail.getValue();

        if (currentList == null) return;

        EMemberEmailInfo loEmail = laEmail.getValue().get(index);
        loEmail.setCRecdStat(fsStatus);

        currentList.set(index, loEmail);
        laEmail.setValue(currentList);
    }

    public String GetCurrentDate(){
        return poAccount.GetCurrentDate();
    }

    public String GetCurrentDateTime(){
        return poAccount.GetCurrentDateTime();
    }

    public String GetUserID(){
        return poAccount.GetUserID();
    }

    public LiveData<List<String>> GetSponsorList(){
        return laSponsors;
    }

    public LiveData<List<ELodgeInfo>> GetLodgeList() {
        return poAccount.GetLodges();
    }

    public LiveData<List<ETitle>> GetTitleList(){
        return poAccount.GetTitleList();
    }

    public LiveData<List<DTownInfo.TownProvince>> SearchTown(String fsSearch){
        return poAccount.SearchTown(fsSearch);
    }
    public LiveData<List<DTownInfo.TownProvince>> HasNewAddress(){
        return laAddress;
    }

    public LiveData<List<EMemberContactInfo>> HasNewContact(){
        return laContact;
    }

    public LiveData<List<EMemberEmailInfo>> HasNewEmail(){
        return laEmail;
    }

    public LiveData<EMemberInfo> GetMemberGLPID(String fsGLPIDxx){
        return poAccount.GetMemberGLPID(fsGLPIDxx);
    }

    public List<DTownInfo.TownProvince> GetMemberAddress(String fsMemberID){
        return poAccount.GetMemberAddress(fsMemberID);
    }

    public List<EMemberContactInfo> GetMemberContact(String fsMemberID){
        return poAccount.GetMemberContact(fsMemberID);
    }

    public List<EMemberEmailInfo> GetMemberEmail(String fsMemberID){
        return poAccount.GetMemberEmail(fsMemberID);
    }

    public List<String> GetCivilStatus(){

        List<String> laCivil = new ArrayList<>();
        Collections.addAll(laCivil,
                MEMBER_STATUS.URL_STATUS_SINGLE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_MARRIED.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_WIDOWED.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_SEPARATED.getFsDescr()
        );
        return laCivil;
    }

    public List<String> GetAccountStatus(){

        List<String> laAccount = new ArrayList<>();
        Collections.addAll(laAccount,
                MEMBER_STATUS.URL_STATUS_INACTIVE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_ACTIVE.getFsDescr(),
                MEMBER_STATUS.URL_STATUS_SUSPENDED.getFsDescr()
        );
        return laAccount;
    }

    public String GenerateGLPID(){
        return poAccount.GenerateGLPID();
    }

    public static class TownCityAdapter extends ArrayAdapter<DTownInfo.TownProvince> {

        private final Context loContext;
        private final List<DTownInfo.TownProvince> towncity;
        private List<DTownInfo.TownProvince> towncityFiltered;

        public TownCityAdapter(@NonNull Context context, int resource, @NonNull List<DTownInfo.TownProvince> objects) {
            super(context, resource, objects);

            loContext = context;
            towncity = objects;
            towncityFiltered = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(towncityFiltered.get(position).getPsTownProvNme());

            return view;

        }

        @Override
        public int getCount() {
            return towncityFiltered.size();
        }

        @Nullable
        @Override
        public DTownInfo.TownProvince getItem(int position) {
            return towncityFiltered.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    List<DTownInfo.TownProvince> results = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(towncity);
                    } else {
                        for (DTownInfo.TownProvince town : towncity) {
                            if (town.getPsTownProvNme().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    town.getPsAddressx().toLowerCase().contains(constraint.toString().toLowerCase())) {

                                results.add(town);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    towncityFiltered = (List<DTownInfo.TownProvince>) results.values;

                    notifyDataSetChanged();
                }
            };
        }
    }

    public void SubmitParameters(EMemberInfo memberInfo, List<EMemberAddress> laAddress, List<EMemberContactInfo> laContact, List<EMemberEmailInfo> laEmail, OnSubmit foCallback){

        foCallback.OnLoad();

        //run first background to get the result
        poAccount.CreateMember(memberInfo)
                .thenCompose(result -> {

                    //if return is booleen
                    if (result instanceof Boolean && !(Boolean) result) {
                        foCallback.OnFailed(poAccount.GetMessage());
                        return CompletableFuture.completedFuture(false);
                    }

                    //if return is empty string
                    if (result instanceof String && (((String) result).isEmpty())) {
                        foCallback.OnFailed("Could not save member address. Member ID not found");
                        return CompletableFuture.completedFuture(false);
                    }
                    String lsMemberID = (String) result;

                    //initialize result holder, return true as default
                    CompletableFuture<Boolean> chain = CompletableFuture.completedFuture(true);

                    // Save addresses one by one
                    for (EMemberAddress loAddress : new HashSet<>(laAddress)) {
                        loAddress.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.CreateMemberAddress(loAddress);
                        });
                    }

                    // Save contacts one by one
                    for (EMemberContactInfo loContact : new HashSet<>(laContact)) {
                        loContact.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.CreateMemberContact(loContact);
                        });
                    }

                    // Save emails one by one, with the generated member id from result
                    for (EMemberEmailInfo loEmail : new HashSet<>(laEmail)) {
                        loEmail.setSMemberID(lsMemberID);

                        //bind the result after each thread runs
                        chain = chain.thenCompose(ok -> {
                            if (!ok) return CompletableFuture.completedFuture(false);
                            return poAccount.CreateMemberEmail(loEmail);
                        });
                    }

                    return chain;

                })
                .thenAccept(allOk -> {
                    foCallback.OnLoad();
                    if (allOk) {
                        foCallback.OnSuccess();
                    } else {
                        foCallback.OnFailed(poAccount.GetMessage());
                    }
                })
                .exceptionally(e -> {
                    foCallback.OnFailed("Could not make request at this moment:\n\n" + e.getMessage());
                    return null;
                });

    }
}
