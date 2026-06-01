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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class VM_Member extends AndroidViewModel {

    private final MutableLiveData<List<String>> laSponsors;
    private final MutableLiveData<String> lsTownSearch;
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
        lsTownSearch = new MutableLiveData<>();
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

    public void AddMemberEmail(String fsEmailID, String fsMemberID, String fsEmailAdd, String fsStatus){

        EMemberEmailInfo loContact = new EMemberEmailInfo(
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
        currentList.add(loContact);
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

    public void SubmitParameters(UserAccount.MemberName memberName, EMemberInfo memberInfo, List<EMemberAddress> laAddress, List<EMemberContactInfo> laContact, List<EMemberEmailInfo> laEmail, OnSubmit foCallback){

        poAccount.CreateMember(memberName, memberInfo)
                .thenCompose(result -> {

                    if (result instanceof Boolean && !(Boolean) result) {
                        foCallback.OnFailed(poAccount.GetMessage());
                        return CompletableFuture.completedFuture(false);
                    }

                    if (result instanceof String && (((String) result).isEmpty())) {
                        foCallback.OnFailed("Could not save member address. Member ID not found");
                        return CompletableFuture.completedFuture(false);
                    }
                    String lsMemberID = (String) result;

                    List<CompletableFuture<Boolean>> memberTask = new ArrayList<>();

                    for (EMemberAddress loAddress : laAddress) {
                        loAddress.setSMemberID(lsMemberID);
                        memberTask.add(poAccount.CreateMemberAddress(loAddress));
                    }

                    for (EMemberContactInfo loContact : laContact) {
                        loContact.setSMemberID(lsMemberID);
                        memberTask.add(poAccount.CreateMemberContact(loContact));
                    }

                    for (EMemberEmailInfo loEmail : laEmail) {
                        loEmail.setSMemberID(lsMemberID);
                        memberTask.add(poAccount.CreateMemberEmail(loEmail));
                    }

                    // Combine all tasks into one future that returns Boolean
                    return CompletableFuture.allOf(memberTask.toArray(new CompletableFuture[0]))
                            .thenApply(v -> memberTask.stream().allMatch(CompletableFuture::join));

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
}
