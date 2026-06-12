package com.gag.useraccount.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.Dialog.Dialog_Add_Member_Info;
import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.LodgeAdapter;
import org.gag.appdriver.App.Adapters.MemberAddressAdapter;
import org.gag.appdriver.App.Adapters.MemberContactAdapter;
import org.gag.appdriver.App.Adapters.MemberEmailAdapter;
import org.gag.appdriver.App.Adapters.TitleAdapter;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.DataObject.DTownInfo;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.ETitle;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Member extends Fragment {

    private Message_Dialog poMessage;
    private LoadDialog poDialog;
    private VM_Member mviewModel;

    private MaterialAutoCompleteTextView
            auto_status,
            auto_title,
            auto_sponosr,
            auto_town,
            auto_contact,
            auto_email,
            auto_civil,
            auto_lodge;

    private ImageButton
            btn_add_address,
            btn_add_contact,
            btn_add_email;

    private MaterialButton btn_add_sponsor, btn_save_sponsor, btn_create, btn_save_address, btn_save_contact, btn_save_email;

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix,
            tie_birthdate,
            tie_glpid,
            tie_address,
            tie_remarks;

    private CheckBox chkbx_homeaddr, chkbx_active, chkbx_activecontact, chkbx_activeemail;

    private Dialog_Add_Member_Info poDialogAddMember;

    private LodgeAdapter LodgeAdapter;
    private TitleAdapter TitleAdapter;
    private MemberAddressAdapter MemberAddressAdapter;
    private MemberContactAdapter MemberContactAdapter;
    private MemberEmailAdapter MemberEmailAdapter;

    private List<String> paramSponsors = new ArrayList<>();
    private List<TownProvince> paramTownProvince= new ArrayList<>();
    private List<EMemberContactInfo> paramContact= new ArrayList<>();
    private List<EMemberEmailInfo> paramEmail= new ArrayList<>();

    private String lsSelectSponsor;
    private String lsSelectLodge;
    private String lsSelectTitle;

    private EMemberInfo loMemberInfo;
    private TownProvince loSelectAddress;
    private EMemberContactInfo loSelectContact;
    private EMemberEmailInfo loSelectEmail;

    private int lnSelectSponsor = -1, lnSelectCivil = -1, lnSelectStatus = -1, lnSelectAddress = -1, lnSelectContact = -1, lnSelectEmail = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_member, container, false);

        mviewModel = new ViewModelProvider(this).get(VM_Member.class);
        poMessage = new Message_Dialog(requireActivity());
        poDialog = new LoadDialog(requireActivity());
        poDialogAddMember = new Dialog_Add_Member_Info(requireActivity(), mviewModel, getViewLifecycleOwner());

        poMessage.InitDialog();
        poDialog.InitDialog();


        initViews(view);
        initDataReceiver();
        initListeners();

        return view;

    }

    private boolean isMemberDataValid(){

        if (tie_glpid.getText() == null || TextUtils.isEmpty(tie_glpid.getText().toString())){
            Toast.makeText(requireActivity(), "GLPID is missing.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (lsSelectLodge == null || lsSelectLodge.isEmpty()){
            Toast.makeText(requireActivity(), "Please select a lodge", Toast.LENGTH_SHORT).show();
            auto_lodge.requestFocus();
            return false;
        }else if (lnSelectStatus < 0){
            Toast.makeText(requireActivity(), "Please select account status", Toast.LENGTH_SHORT).show();
            auto_lodge.requestFocus();
            return false;
        }else if (lsSelectTitle == null || lsSelectTitle.isEmpty()){
            Toast.makeText(requireActivity(), "Please select a title", Toast.LENGTH_SHORT).show();
            auto_title.requestFocus();
            return false;
        }else if (tie_lastname.getText() == null || tie_lastname.getText().toString().isEmpty()){
            Toast.makeText(requireActivity(), "Please enter lastname", Toast.LENGTH_SHORT).show();
            tie_lastname.requestFocus();
            return false;
        }else if (tie_firstname.getText() == null || tie_firstname.getText().toString().isEmpty()){
            Toast.makeText(requireActivity(), "Please enter firstname", Toast.LENGTH_SHORT).show();
            tie_firstname.requestFocus();
            return false;
        }else if (lnSelectCivil < 0){
            Toast.makeText(requireActivity(), "Please select civil status", Toast.LENGTH_SHORT).show();
            auto_civil.requestFocus();
            return false;
        }else if (paramTownProvince.size() < 1){
            Toast.makeText(requireActivity(), "Please enter atleast one address", Toast.LENGTH_SHORT).show();
            auto_civil.requestFocus();
            return false;
        }else if (paramContact.size() < 1){
            Toast.makeText(requireActivity(), "Please enter atleast one contact", Toast.LENGTH_SHORT).show();
            auto_civil.requestFocus();
            return false;
        }else if (paramEmail.size() < 1){
            Toast.makeText(requireActivity(), "Please enter atleast one email", Toast.LENGTH_SHORT).show();
            auto_civil.requestFocus();
            return false;
        }
        return true;
    }

    private void initViews(View view) {

        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_title = view.findViewById(R.id.auto_title);
        auto_sponosr = view.findViewById(R.id.auto_sponosr);
        btn_save_sponsor = view.findViewById(R.id.btn_save_sponsor);
        auto_town = view.findViewById(R.id.auto_town);
        auto_contact = view.findViewById(R.id.auto_contact);
        auto_email = view.findViewById(R.id.auto_email);
        auto_civil = view.findViewById(R.id.auto_civil);

        tie_birthdate = view.findViewById(R.id.tie_birthdate);
        tie_address = view.findViewById(R.id.tie_address);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        btn_add_sponsor = view.findViewById(R.id.btn_add_sponsor);
        btn_add_address = view.findViewById(R.id.btn_add_address);
        btn_add_contact = view.findViewById(R.id.btn_add_contact);
        btn_add_email = view.findViewById(R.id.btn_add_email);
        btn_create = view.findViewById(R.id.btn_create);
        btn_save_address = view.findViewById(R.id.btn_save_address);
        btn_save_contact = view.findViewById(R.id.btn_save_contact);
        btn_save_email = view.findViewById(R.id.btn_save_email);

        chkbx_active = view.findViewById(R.id.chkbx_active);
        chkbx_homeaddr = view.findViewById(R.id.chkbx_homeaddr);
        chkbx_activecontact = view.findViewById(R.id.chkbx_activecontact);
        chkbx_activeemail = view.findViewById(R.id.chkbx_activeemail);

        tie_lastname = view.findViewById(R.id.tie_lastname);
        tie_firstname = view.findViewById(R.id.tie_firstname);
        tie_middlename = view.findViewById(R.id.tie_middlename);
        tie_suffix = view.findViewById(R.id.tie_suffix);
        tie_glpid = view.findViewById(R.id.tie_glpid);

    }

    private void initDataReceiver() {

        if (getArguments() == null){
            btn_create.setText("Create Member");
            tie_glpid.setText(mviewModel.GenerateGLPID());
        }else {

            if (getArguments().getString("fsGLPIDxx") == null || getArguments().getString("fsGLPIDxx").isEmpty()){

                poMessage.ShowMessage(1, "Could not verify member ID", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .remove(Fragment_Member.this)
                                .commit();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
                return;
            }
            btn_create.setText("Update Member");
            tie_glpid.setText(getArguments().getString("fsGLPIDxx"));
        }

        if (tie_glpid.getText() == null || tie_glpid.getText().toString().isEmpty()){
            Toast.makeText(requireActivity(), "GLP ID is not initialized properly.", Toast.LENGTH_SHORT).show();
            return;
        }

        auto_status.setAdapter(
                new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        mviewModel.GetAccountStatus()
                ));

        auto_civil.setAdapter(
                new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        mviewModel.GetCivilStatus()
                ));

        //get member information via glpid, this is to restore the new entry if error occured or to update the member
        mviewModel.GetMemberGLPID(tie_glpid.getText().toString()).observe(getViewLifecycleOwner(), new Observer<EMemberInfo>() {
            @Override
            public void onChanged(EMemberInfo eMemberInfo) {

                //do not proceed if member information is not found
                if (eMemberInfo == null){

                    //if member information is not found via argument's passed GLPID (update member only), validate and return
                    if (getArguments() != null){

                        poMessage.ShowMessage(1, "Could not load member information", "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(Fragment_Member.this)
                                        .commit();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                        return;
                    }
                    return;
                }

                if (getArguments() != null){

                    mviewModel.DownloadMemberInfo(eMemberInfo.getSMemberID(), new VM_Member.OnDownload() {
                        @Override
                        public void Loading() {
                            Toast.makeText(requireActivity(), "Downloading member information...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void Finished(String fsMessage) {
                            Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                loMemberInfo = eMemberInfo;

                //initialize address, and email with exisitng list
                mviewModel.GetMemberAddress(loMemberInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<TownProvince>>() {
                    @Override
                    public void onChanged(List<TownProvince> townProvinces) {

                        if (townProvinces == null) return;

                        mviewModel.ClearAddress();
                        for (TownProvince loTown : townProvinces){

                            mviewModel.AddMemberAddress(
                                    loTown.getPsAddrsIDx(),
                                    loTown.getPsTownIDxx(),
                                    loTown.getPsProvIDxx(),
                                    loTown.getPsTownProvNme(),
                                    loTown.getPsAddressx(),
                                    loTown.isHomeAddr(),
                                    loTown.isActive()
                            );
                        }
                    }
                });

                //initialize contact with exisitng list
                mviewModel.GetMemberContact(loMemberInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<EMemberContactInfo>>() {
                    @Override
                    public void onChanged(List<EMemberContactInfo> eMemberContactInfos) {

                        if (eMemberContactInfos == null) return;

                        mviewModel.ClearContacts();
                        for (EMemberContactInfo loContact : eMemberContactInfos){

                            mviewModel.AddMemberContact(
                                    loContact.getSContctID(),
                                    loContact.getSMemberID(),
                                    loContact.getSContctNo(),
                                    loContact.getSRemarksx(),
                                    loContact.getCRecdStat()
                            );
                        }
                    }
                });

                //initialize email with exisitng list
                mviewModel.GetMemberEmail(loMemberInfo.getSMemberID()).observe(getViewLifecycleOwner(), new Observer<List<EMemberEmailInfo>>() {
                    @Override
                    public void onChanged(List<EMemberEmailInfo> eMemberEmailInfos) {

                        mviewModel.ClearEmails();
                        for (EMemberEmailInfo loEmail : eMemberEmailInfos){

                            mviewModel.AddMemberEmail(
                                    loEmail.getSMailIDxx(),
                                    loEmail.getSMemberID(),
                                    loEmail.getSEmailAdd(),
                                    loEmail.getCRecdStat()
                            );
                        }
                    }
                });

                //initialize member name and birthdate
                tie_lastname.setText(loMemberInfo.getSLastName());
                tie_firstname.setText(loMemberInfo.getSFrstName());
                tie_middlename.setText(loMemberInfo.getSMiddName());
                tie_suffix.setText(loMemberInfo.getSSuffixNm());
                tie_birthdate.setText(loMemberInfo.getDBirthDte());

                //initialze sponsors, if not empty
                if (!(loMemberInfo.getSSponsor1() == null ? "" : loMemberInfo.getSSponsor1()).isEmpty()) mviewModel.AddSponsor(loMemberInfo.getSSponsor1());
                if (!(loMemberInfo.getSSponsor2() == null ? "" : loMemberInfo.getSSponsor2()).isEmpty()) mviewModel.AddSponsor(loMemberInfo.getSSponsor2());
                if (!(loMemberInfo.getSSponsor3() == null ? "" : loMemberInfo.getSSponsor3()).isEmpty()) mviewModel.AddSponsor(loMemberInfo.getSSponsor3());

                auto_status.setText(mviewModel.GetAccountStatus().get(Integer.parseInt(loMemberInfo.getCMmbrStat() == null ? "0" : loMemberInfo.getCMmbrStat())), false);
                lnSelectStatus = Integer.parseInt(loMemberInfo.getCMmbrStat());

                auto_civil.setText(mviewModel.GetCivilStatus().get(Integer.parseInt(loMemberInfo.getCCvilStat() == null ? "0" : loMemberInfo.getCCvilStat())), false);
                lnSelectCivil = Integer.parseInt(loMemberInfo.getCCvilStat());
            }
        });

        mviewModel.GetSponsorList().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {

                paramSponsors = strings;

                auto_sponosr.setAdapter(new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        strings
                ));
                auto_sponosr.postDelayed(() -> auto_sponosr.showDropDown(), 200);
            }
        });

        mviewModel.GetLodgeList().observe(getViewLifecycleOwner(), new Observer<List<ELodgeInfo>>() {
            @Override
            public void onChanged(List<ELodgeInfo> eLodgeInfos) {

                if (eLodgeInfos.size() < 1) return;

                LodgeAdapter = new LodgeAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eLodgeInfos);

                auto_lodge.setAdapter(LodgeAdapter);

                //enable selection if more than 1 item
                if (eLodgeInfos.size() > 1){
                    auto_lodge.setEnabled(true);
                }else {
                    auto_lodge.setEnabled(false);

                    //select first item
                    auto_lodge.setText(LodgeAdapter.getItem(0).getSLodgeNme(), false);
                    lsSelectLodge = LodgeAdapter.getItem(0).getSLodgeIDx();
                }

                if (loMemberInfo == null) return;

                //display the lodge information if member information is set
                for (int index = 0; index <  LodgeAdapter.lodges.size(); index++){

                    if (loMemberInfo.getSLodgeIDx().equalsIgnoreCase( LodgeAdapter.lodges.get(index).getSLodgeIDx())){

                        auto_lodge.setText( LodgeAdapter.lodges.get(index).getSLodgeNme(), false);
                        lsSelectLodge = LodgeAdapter.lodges.get(index).getSLodgeIDx();
                    }
                }
            }
        });

        mviewModel.GetTitleList().observe(getViewLifecycleOwner(), new Observer<List<ETitle>>() {
            @Override
            public void onChanged(List<ETitle> eTitles) {

                TitleAdapter = new TitleAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eTitles
                );
                auto_title.setAdapter(TitleAdapter);

                if (loMemberInfo == null) return;

                //display the title information if member information is set
                for (int index = 0; index < TitleAdapter.titles.size(); index++){

                    if (loMemberInfo.getSTitleIDx().equalsIgnoreCase(TitleAdapter.titles.get(index).getSTitleIDx())){

                        auto_title.setText(TitleAdapter.titles.get(index).getSTitleDsc(), false);
                        lsSelectTitle = TitleAdapter.titles.get(index).getSTitleIDx();
                    }
                }
            }
        });

        mviewModel.HasNewAddress().observe(getViewLifecycleOwner(), new Observer<List<TownProvince>>() {
            @Override
            public void onChanged(List<TownProvince> memberAddresses) {

                //initialize parameter, list for address entry
                paramTownProvince = memberAddresses;

                MemberAddressAdapter = new MemberAddressAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        memberAddresses
                );
                auto_town.setAdapter(MemberAddressAdapter);
                auto_town.postDelayed(() -> auto_town.showDropDown(), 200);
            }
        });

        mviewModel.HasNewContact().observe(getViewLifecycleOwner(), new Observer<List<EMemberContactInfo>>() {
            @Override
            public void onChanged(List<EMemberContactInfo> eMemberContactInfos) {

                paramContact = eMemberContactInfos;

                MemberContactAdapter = new MemberContactAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eMemberContactInfos
                );
                auto_contact.setAdapter(MemberContactAdapter);
                auto_contact.postDelayed(() -> auto_contact.showDropDown(), 200);
            }
        });

        mviewModel.HasNewEmail().observe(getViewLifecycleOwner(), new Observer<List<EMemberEmailInfo>>() {
            @Override
            public void onChanged(List<EMemberEmailInfo> eMemberEmailInfos) {

                paramEmail = eMemberEmailInfos;

                MemberEmailAdapter = new MemberEmailAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eMemberEmailInfos
                );
                auto_email.setAdapter(MemberEmailAdapter);
                auto_email.postDelayed(() -> auto_email.showDropDown(), 200);
            }
        });
    }

    private void initListeners() {

        tie_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();

                new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tie_birthdate.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        auto_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lsSelectLodge = ((ELodgeInfo) adapterView.getItemAtPosition(i)).getSLodgeIDx();
                auto_lodge.setText(((ELodgeInfo) adapterView.getItemAtPosition(i)).getSLodgeNme(), false);
            }
        });

        auto_civil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lnSelectCivil = i;
            }
        });

        auto_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lnSelectStatus = i;
            }
        });

        auto_title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lsSelectTitle = ((ETitle) adapterView.getItemAtPosition(i)).getSTitleIDx();
                auto_title.setText(((ETitle) adapterView.getItemAtPosition(i)).getSTitleDsc(), false);
            }
        });

        /*MEMBER SPONSOR LISTENER*/
        auto_sponosr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectSponsor = i;
                lsSelectSponsor = adapterView.getItemAtPosition(i).toString();


                auto_sponosr.setText(adapterView.getItemAtPosition(lnSelectSponsor).toString());
                btn_save_sponsor.setVisibility(View.VISIBLE);
            }
        });

        auto_sponosr.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (lnSelectSponsor < 0) {
                    if (btn_save_sponsor.getVisibility() == View.VISIBLE) btn_save_sponsor.setVisibility(View.GONE);
                }else {
                    if (btn_save_sponsor.getVisibility() == View.GONE) btn_save_sponsor.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_add_sponsor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poDialogAddMember.ShowSponsor(true, auto_sponosr.getText() == null ? "" : auto_sponosr.getText().toString(), new Dialog_Add_Member_Info.OnSponsor() {
                    @Override
                    public void OnSubmit(String fsSponsor) {

                        if (!mviewModel.AddSponsor(fsSponsor)){

                            poMessage.ShowMessage(1, "Sponsor has reached maximum limit of entries", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {
                                }
                            });
                            return;
                        }

                        //reset selection
                        lnSelectSponsor = -1;
                        ClearFields(new ArrayList<>(List.of(auto_sponosr, btn_save_sponsor)), false);
                    }
                });
            }
        });

        btn_save_sponsor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lnSelectSponsor < 0){
                    Toast.makeText(requireActivity(), "Please select a sponsor", Toast.LENGTH_SHORT).show();
                    return;
                }

                poDialogAddMember.ShowSponsor(false, lsSelectSponsor, new Dialog_Add_Member_Info.OnSponsor() {
                    @Override
                    public void OnSubmit(String fsSponsor) {

                        mviewModel.ReplaceSponsor(
                                lnSelectSponsor,
                                fsSponsor
                        );

                        //reset selection
                        lnSelectSponsor = -1;
                        ClearFields(new ArrayList<>(List.of(auto_sponosr, btn_save_sponsor)), false);
                    }
                });
            }
        });

        /*MEMBER ADDRESS LISTENER*/

        auto_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectAddress = (TownProvince) adapterView.getItemAtPosition(i);
                lnSelectAddress = i;

                auto_town.setText(loSelectAddress.getPsTownProvNme(), false);
                tie_address.setText(loSelectAddress.getPsAddressx());
                chkbx_homeaddr.setChecked(loSelectAddress.isHomeAddr().equals("1"));
                chkbx_active.setChecked((loSelectAddress.isActive().equals("1")));
            }
        });

        auto_town.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (MemberAddressAdapter == null) return;
                MemberAddressAdapter.getFilter().filter(charSequence);
            }
        });

        tie_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectAddress == null) return;

                //show save button, if text changes
                if (!charSequence.toString().equalsIgnoreCase(loSelectAddress.getPsAddressx())){
                    //show if gone
                    if (btn_save_address.getVisibility() == View.GONE) btn_save_address.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_address.getVisibility() == View.VISIBLE) btn_save_address.setVisibility(View.GONE);
                }
            }
        });

        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poDialogAddMember.ShowAddress(new Dialog_Add_Member_Info.OnAddress() {
                    @Override
                    public void OnAddress(TownProvince loProvince) {

                        mviewModel.AddMemberAddress(
                                "",
                                loProvince.getPsTownIDxx(),
                                loProvince.getPsProvIDxx(),
                                loProvince.getPsTownProvNme(),
                                loProvince.getPsAddressx(),
                                loProvince.isHomeAddr(),
                                loProvince.isActive()

                        );
                    }
                });

                //reset selection
                loSelectAddress = null;
                lnSelectAddress = -1;

                ClearFields(new ArrayList<>(List.of(auto_town, tie_address, chkbx_homeaddr, chkbx_active)), false);
            }
        });

        btn_save_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.ReplaceAddress(
                        lnSelectAddress,
                        new TownProvince(
                                loSelectAddress.getPsAddrsIDx(),
                                loSelectAddress.getPsTownIDxx(),
                                loSelectAddress.getPsProvIDxx(),
                                loSelectAddress.getPsTownProvNme(),
                                tie_address.getText() == null ? "" : tie_address.getText().toString(),
                                chkbx_homeaddr.isChecked() ? "1" : "0",
                                chkbx_active.isChecked() ? "1" : "0"
                        )
                );

                //reset selection
                loSelectAddress = null;
                lnSelectAddress = -1;

                ClearFields(new ArrayList<>(List.of(auto_town, tie_address, chkbx_homeaddr, chkbx_active, btn_save_address)), false);
            }
        });

        chkbx_homeaddr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

                if (loSelectAddress == null) return;

                //show save button, if selection changed
                if (!loSelectAddress.isHomeAddr().equalsIgnoreCase(b ? "1" : "0")){
                    //show if gone
                    if (btn_save_address.getVisibility() == View.GONE) btn_save_address.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_address.getVisibility() == View.VISIBLE) btn_save_address.setVisibility(View.GONE);
                }
            }
        });

        chkbx_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

                if (loSelectAddress == null) return;

                //show save button, if selection changed
                if (!loSelectAddress.isActive().equalsIgnoreCase(b ? "1" : "0")){
                    //show if gone
                    if (btn_save_address.getVisibility() == View.GONE) btn_save_address.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_address.getVisibility() == View.VISIBLE) btn_save_address.setVisibility(View.GONE);
                }
            }
        });

        /*MEMBER CONTACT LISTENER*/

        auto_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectContact = (EMemberContactInfo) adapterView.getItemAtPosition(i);
                lnSelectContact = i;

                auto_contact.setText(loSelectContact.getSContctNo(), false);
                tie_remarks.setText(loSelectContact.getSRemarksx());
                chkbx_activecontact.setChecked(loSelectContact.getCRecdStat().equalsIgnoreCase("1"));

            }
        });

        auto_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (MemberContactAdapter == null) return;
                MemberContactAdapter.getFilter().filter(charSequence);
            }
        });

        tie_remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectContact == null) return;

                //show save button, if text changes
                if (!charSequence.toString().equalsIgnoreCase(loSelectContact.getSRemarksx())){
                    //show if gone
                    if (btn_save_contact.getVisibility() == View.GONE) btn_save_contact.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_contact.getVisibility() == View.VISIBLE) btn_save_contact.setVisibility(View.GONE);
                }
            }
        });

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poDialogAddMember.ShowContact(new Dialog_Add_Member_Info.OnContact() {
                    @Override
                    public void OnContact(String lsContactNo, String lsRemarks, String lsActive) {

                        mviewModel.AddMemberContact(
                                "",
                                "",
                                lsContactNo,
                                lsRemarks,
                                lsActive
                        );
                    }
                });

                loSelectContact = null;
                lnSelectContact = -1;

                ClearFields(new ArrayList<>(List.of(auto_contact, tie_remarks, chkbx_activecontact)), false);
            }
        });

        btn_save_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.ReplaceContact(
                        lnSelectContact,
                        tie_remarks.getText() == null ? "" : tie_remarks.getText().toString(),
                        chkbx_activecontact.isChecked() ? "1" : "0"
                );

                loSelectContact = null;
                lnSelectContact = -1;

                ClearFields(new ArrayList<>(List.of(auto_contact, tie_remarks, chkbx_activecontact, btn_save_contact)), false);
            }
        });

        chkbx_activecontact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

                if (loSelectContact == null) return;

                //show save button, if text changes
                if (!loSelectContact.getCRecdStat().equalsIgnoreCase(b ? "1" : "0")){
                    //show if gone
                    if (btn_save_contact.getVisibility() == View.GONE) btn_save_contact.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_contact.getVisibility() == View.VISIBLE) btn_save_contact.setVisibility(View.GONE);
                }
            }
        });

        /*MEMBER EMAIL LISTENER*/

        auto_email.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectEmail = (EMemberEmailInfo) adapterView.getItemAtPosition(i);
                lnSelectEmail = i;

                auto_email.setText(loSelectEmail.getSEmailAdd(), false);
                chkbx_activeemail.setChecked(loSelectEmail.getCRecdStat().equalsIgnoreCase("1"));
            }
        });

        auto_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (MemberEmailAdapter == null) return;
                MemberEmailAdapter.getFilter().filter(charSequence);
            }
        });

        btn_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poDialogAddMember.ShowEmail(new Dialog_Add_Member_Info.OnEmail() {
                    @Override
                    public void OnEmail(String lsEmail, String lsActive) {

                        mviewModel.AddMemberEmail(
                                "",
                                "",
                                lsEmail,
                                lsActive
                        );
                    }
                });

                loSelectEmail = null;
                lnSelectEmail = -1;

                ClearFields(new ArrayList<>(List.of(auto_email, chkbx_activeemail)), false);
            }
        });

        btn_save_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.ReplaceEmail(
                        lnSelectEmail,
                        chkbx_activeemail.isChecked() ? "1" : "0"
                );

                loSelectEmail = null;
                lnSelectEmail = -1;

                ClearFields(new ArrayList<>(List.of(auto_email, chkbx_activeemail, btn_save_email)), false);
            }
        });

        chkbx_activeemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

                if (loSelectEmail == null) return;

                //show save button, if selection changed
                if (!loSelectEmail.getCRecdStat().equalsIgnoreCase(b ? "1" : "0")){
                    //show if gone
                    if (btn_save_email.getVisibility() == View.GONE) btn_save_email.setVisibility(View.VISIBLE);
                }else {
                    //hide is visible
                    if (btn_save_email.getVisibility() == View.VISIBLE) btn_save_email.setVisibility(View.GONE);
                }
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validate member data information
                if (!isMemberDataValid()) return;

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poConfirmDialog) {
                        poConfirmDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poConfirmDialog) {

                        poConfirmDialog.dismiss();

                        String lsFrstNme= tie_firstname.getText() == null || tie_firstname.getText().toString().isEmpty() ? "" : tie_firstname.getText().toString();
                        String lsMiddNme= tie_middlename.getText() == null || tie_middlename.getText().toString().isEmpty() ? "" : tie_middlename.getText().toString();
                        String lsLastNme= tie_lastname.getText() == null || tie_lastname.getText().toString().isEmpty() ? "" : tie_lastname.getText().toString();
                        String lsSuffix= tie_suffix.getText() == null || tie_suffix.getText().toString().isEmpty() ? "" : tie_suffix.getText().toString();

                        //if member info is not existing, create a new one
                        if (loMemberInfo == null || loMemberInfo.getSMemberID().isEmpty()){

                            loMemberInfo = new EMemberInfo(
                                    "",
                                    lsSelectLodge,
                                    tie_glpid.getText() == null ? "" : tie_glpid.getText().toString(),
                                    lsLastNme,
                                    lsFrstNme,
                                    lsMiddNme,
                                    lsSuffix,
                                    String.valueOf(lnSelectCivil),
                                    tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString(),
                                    String.valueOf(lnSelectStatus),
                                    mviewModel.GetCurrentDate(),
                                    lnSelectStatus > 1 ? mviewModel.GetCurrentDateTime() : null,
                                    lsSelectTitle,
                                    null,
                                    null,
                                    null,
                                    null,
                                    "",
                                    "",
                                    "",
                                    0.00,
                                    0.00,
                                    null,
                                    lnSelectStatus != 1 ? "0" : "1"
                            );
                        }else {

                            //if member status has changed
                            if (lnSelectStatus != Integer.parseInt(loMemberInfo.getCMmbrStat() == null ? "0" : loMemberInfo.getCMmbrStat())){

                                if (lnSelectStatus > 1){ //update suspend date, if status is suspended
                                    loMemberInfo.setDSuspendx(mviewModel.GetCurrentDate());
                                }else { //remove suspend date
                                    loMemberInfo.setDSuspendx(null);
                                }
                            }

                            loMemberInfo.setSLodgeIDx(lsSelectLodge);
                            loMemberInfo.setSLastName(lsLastNme);
                            loMemberInfo.setSFrstName(lsFrstNme);
                            loMemberInfo.setSMiddName(lsMiddNme);
                            loMemberInfo.setSSuffixNm(lsSuffix);
                            loMemberInfo.setCCvilStat(String.valueOf(lnSelectCivil));
                            loMemberInfo.setDBirthDte(tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString());
                            loMemberInfo.setCMmbrStat(String.valueOf(lnSelectStatus));
                            loMemberInfo.setSTitleIDx(lsSelectTitle);
                            loMemberInfo.setCRecdStat(String.valueOf(lnSelectStatus));
                        }

                        int hasActive = 0;
                        int hasHomeAddr = 0;

                        List<EMemberAddress> laAddressParams = new ArrayList<>();
                        for (TownProvince townProvince : paramTownProvince){
                            Log.d("Address added ", townProvince.getPsTownProvNme());

                            String lsAddress;
                            if (townProvince.getPsAddrsIDx() == null || townProvince.getPsAddrsIDx().isEmpty()){
                                lsAddress = townProvince.getPsAddressx() + ", " + townProvince.getPsTownProvNme();
                            }else {
                                lsAddress = townProvince.getPsAddressx();
                            }

                            laAddressParams.add(
                                    new EMemberAddress(
                                            townProvince.getPsAddrsIDx(),
                                            loMemberInfo == null ? "" : loMemberInfo.getSMemberID(),
                                            lsAddress,
                                            townProvince.getPsTownIDxx(),
                                            townProvince.isHomeAddr(),
                                            townProvince.isActive(),
                                            mviewModel.GetUserID(),
                                            mviewModel.GetCurrentDate(),
                                            mviewModel.GetCurrentDateTime()
                                    )
                            );

                            //check if  active
                            if (townProvince.isActive().equals("1")) hasActive += 1;

                            //check if home address
                            if (townProvince.isHomeAddr().equals("1")) hasHomeAddr += 1;
                        }

                        //add or update sponsors
                        for (int index = 0; index < paramSponsors.size(); index++){
                            Log.d("Sponsors added ", paramSponsors.get(index));

                            switch (index){

                                case 0:
                                    loMemberInfo.setSSponsor1(paramSponsors.get(index));
                                    break;
                                case 1:
                                    loMemberInfo.setSSponsor2(paramSponsors.get(index));
                                    break;
                                case 2:
                                    loMemberInfo.setSSponsor3(paramSponsors.get(index));
                                    break;
                            }
                        }

                        if (hasHomeAddr < 1){

                            poMessage.ShowMessage(1, "Please select atleast one home address", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        if (hasActive < 1){

                            poMessage.ShowMessage(1, "Please select atleast one active address", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        //reset value
                        hasActive = 0;

                        List<EMemberContactInfo> laContactParams = new ArrayList<>();
                        for (EMemberContactInfo contactInfo : paramContact){
                            Log.d("Contacts added ", contactInfo.getSContctNo());

                            laContactParams.add(
                                    new EMemberContactInfo(
                                            contactInfo.getSContctID(),
                                            contactInfo.getSMemberID(),
                                            contactInfo.getSContctNo(),
                                            contactInfo.getSRemarksx(),
                                            contactInfo.getCRecdStat(),
                                            mviewModel.GetUserID(),
                                            mviewModel.GetCurrentDate(),
                                            mviewModel.GetCurrentDateTime()

                                    )
                            );

                            if (contactInfo.getCRecdStat().equalsIgnoreCase("1")) hasActive += 1;
                        }

                        if (hasActive < 1){

                            poMessage.ShowMessage(1, "Please select atleast one active contact", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        //reset value
                        hasActive = 0;

                        List<EMemberEmailInfo> laEmailParams = new ArrayList<>();
                        for (EMemberEmailInfo eMemberEmailInfo : paramEmail){
                            Log.d("Emails added ", eMemberEmailInfo.getSEmailAdd());

                            laEmailParams.add(
                                    new EMemberEmailInfo(
                                            eMemberEmailInfo.getSMailIDxx(),
                                            eMemberEmailInfo.getSMemberID(),
                                            eMemberEmailInfo.getSEmailAdd(),
                                            eMemberEmailInfo.getCRecdStat(),
                                            mviewModel.GetUserID(),
                                            mviewModel.GetCurrentDate(),
                                            mviewModel.GetCurrentDateTime()
                                    )
                            );

                            if (eMemberEmailInfo.getCRecdStat().equalsIgnoreCase("1")) hasActive += 1;
                        }

                        if (hasActive < 1){

                            poMessage.ShowMessage(1, "Please select atleast one active email", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        mviewModel.SubmitParameters(loMemberInfo, laAddressParams, laContactParams, laEmailParams, new VM_Member.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poDialog.ShowDialog("Submitting your information. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poDialog.DismissDialog();

                                poMessage.ShowMessage(0, "Member information has been saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();


                                        //reset only text from selection
                                        ClearFields(new ArrayList<>(List.of(auto_lodge, auto_title, auto_status, auto_civil)), false);

                                        //clear all fields and adapters
                                        ClearFields(new ArrayList<>(List.of(tie_glpid, auto_sponosr, btn_save_sponsor, tie_lastname,
                                                        tie_firstname, tie_middlename, tie_suffix, tie_birthdate, tie_address,
                                                        auto_town, chkbx_homeaddr, chkbx_homeaddr, auto_contact, tie_remarks, chkbx_activecontact,
                                                        auto_email, chkbx_activeemail, btn_save_address, btn_save_contact, btn_save_email
                                                    )
                                                ),
                                                true);

                                        //reset parameters or data
                                        loMemberInfo = null;
                                        paramTownProvince = null;
                                        paramSponsors = null;
                                        paramContact = null;
                                        paramEmail = null;

                                        mviewModel.ClearSponsor();
                                        mviewModel.ClearAddress();
                                        mviewModel.ClearContacts();
                                        mviewModel.ClearEmails();

                                        requireActivity()
                                                .getSupportFragmentManager()
                                                .popBackStack("create_member", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                                });
                            }

                            @Override
                            public void OnFailed(String fsMesssage) {
                                poDialog.DismissDialog();

                                poMessage.ShowMessage(1, fsMesssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        //retrieve saved entries upon error, if exists
                                        initDataReceiver();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                                });
                            }
                        });

                    }
                });

            }
        });
    }

    private void ClearFields(List<View> fields, boolean clearAdapter) {

        for (View view : fields) {

            if (view instanceof TextInputEditText){
                ((TextInputEditText) view).setText("");
            }else if (view instanceof MaterialAutoCompleteTextView){
                ((MaterialAutoCompleteTextView) view).setText("");
                if (clearAdapter) ((MaterialAutoCompleteTextView) view).setAdapter(null);
            }else if (view instanceof CheckBox){
                ((CheckBox) view).setChecked(false);
            }else if (view instanceof MaterialButton){
                view.setVisibility(View.GONE);
            }
        }
    }

}