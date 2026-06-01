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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.Dialog.Dialog_Add_Member_Info;
import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.gag.appdriver.App.Accounts.UserAccount;
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

public class Fragment_Create_Member extends Fragment {

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

    private MaterialButton btn_add_sponsor, btn_create, btn_save_address, btn_save_contact, btn_save_email;

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
    private List<DTownInfo.TownProvince> paramTownProvince= new ArrayList<>();
    private List<EMemberContactInfo> paramContact= new ArrayList<>();
    private List<EMemberEmailInfo> paramEmail= new ArrayList<>();

    private String lsSelectLodge;
    private String lsSelectTitle;

    private DTownInfo.TownProvince loSelectAddress;
    private EMemberContactInfo loSelectContact;
    private EMemberEmailInfo loSelectEmail;

    private int lnSelectCivil;
    private int lnSelectStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_create_member, container, false);

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

    private void initViews(View view) {

        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_title = view.findViewById(R.id.auto_title);
        auto_sponosr = view.findViewById(R.id.auto_sponosr);
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

                poMessage.ShowMessage(1, "Could not find member information", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .remove(Fragment_Create_Member.this)
                                .commit();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
                return;
            }

            mviewModel.GetMemberGLPID(getArguments().getString("fsGLPIDxx")).observe(requireActivity(), new Observer<EMemberInfo>() {
                @Override
                public void onChanged(EMemberInfo eMemberInfo) {

                    if (eMemberInfo == null){

                        poMessage.ShowMessage(1, "Could not load member information", "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(Fragment_Create_Member.this)
                                        .commit();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                        return;

                    }

                    btn_create.setText("Update Member");
                    tie_glpid.setText(eMemberInfo.getSGLPIDNoX());

                    //initialize address, and email with exisitng list
                    if (mviewModel.GetMemberAddress(eMemberInfo.getSMemberID()).size() > 1){

                        for (DTownInfo.TownProvince loTown : mviewModel.GetMemberAddress(eMemberInfo.getSMemberID())){

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

                    //initialize contact with exisitng list
                    if (mviewModel.GetMemberContact(eMemberInfo.getSMemberID()).size() > 1){

                        for (EMemberContactInfo loContact : mviewModel.GetMemberContact(eMemberInfo.getSMemberID())){

                            mviewModel.AddMemberContact(
                                    loContact.getSContctID(),
                                    loContact.getSMemberID(),
                                    loContact.getSContctNo(),
                                    loContact.getSRemarksx(),
                                    loContact.getCRecdStat()
                            );
                        }
                    }

                    //initialize email with exisitng list
                    if (mviewModel.GetMemberEmail(eMemberInfo.getSMemberID()).size() > 1){

                        for (EMemberEmailInfo loEmail : mviewModel.GetMemberEmail(eMemberInfo.getSMemberID())){

                            mviewModel.AddMemberEmail(
                                    loEmail.getSMailIDxx(),
                                    loEmail.getSMemberID(),
                                    loEmail.getSEmailAdd(),
                                    loEmail.getCRecdStat()
                            );
                        }
                    }
                }
            });
        }

        mviewModel.GetSponsorList().observe(requireActivity(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {

                paramSponsors = strings;

               auto_sponosr.setAdapter(new ArrayAdapter<>(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        strings
                ));
            }
        });

        mviewModel.GetLodgeList().observe(requireActivity(), new Observer<List<ELodgeInfo>>() {
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
            }
        });

        mviewModel.GetTitleList().observe(requireActivity(), new Observer<List<ETitle>>() {
            @Override
            public void onChanged(List<ETitle> eTitles) {

                TitleAdapter = new TitleAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eTitles
                );

                auto_title.setAdapter(TitleAdapter);
            }
        });

        mviewModel.HasNewAddress().observe(requireActivity(), new Observer<List<DTownInfo.TownProvince>>() {
            @Override
            public void onChanged(List<DTownInfo.TownProvince> memberAddresses) {

                //initialize parameter, list for address entry
                paramTownProvince = memberAddresses;

                MemberAddressAdapter = new MemberAddressAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        memberAddresses
                );
                auto_town.setAdapter(MemberAddressAdapter);
            }
        });

        mviewModel.HasNewContact().observe(requireActivity(), new Observer<List<EMemberContactInfo>>() {
            @Override
            public void onChanged(List<EMemberContactInfo> eMemberContactInfos) {

                paramContact = eMemberContactInfos;

                MemberContactAdapter = new MemberContactAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eMemberContactInfos
                );
                auto_contact.setAdapter(MemberContactAdapter);
            }
        });

        mviewModel.HasNewEmail().observe(requireActivity(), new Observer<List<EMemberEmailInfo>>() {
            @Override
            public void onChanged(List<EMemberEmailInfo> eMemberEmailInfos) {

                paramEmail = eMemberEmailInfos;

                MemberEmailAdapter = new MemberEmailAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eMemberEmailInfos
                );
                auto_email.setAdapter(MemberEmailAdapter);

            }
        });

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
    }

    private void initListeners() {

        btn_add_sponsor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (auto_sponosr.getText() == null || auto_sponosr.getText().toString().isEmpty()) {

                    poMessage.ShowMessage(1, "Please enter a sponsor name", "Okay", "", new Message_Dialog.OnDialogClick() {
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

                if (!mviewModel.AddSponsor(auto_sponosr.getText().toString())){

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
                auto_sponosr.setText("");

            }
        });

        btn_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poDialogAddMember.ShowAddress(new Dialog_Add_Member_Info.OnAddress() {
                    @Override
                    public void OnAddress(DTownInfo.TownProvince loProvince) {

                        mviewModel.AddMemberAddress(
                                "",
                                loProvince.getPsTownIDxx(),
                                loProvince.getPsProvIDxx(),
                                loProvince.getPsTownProvNme(),
                                loProvince.getPsAddressx(),
                                loProvince.isHomeAddr(),
                                loProvince.isActive()

                        );

                        ClearFields(new ArrayList<>(List.of(auto_town, tie_address, chkbx_homeaddr, chkbx_active)), false);
                    }
                });
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

                        ClearFields(new ArrayList<>(List.of(auto_contact, tie_remarks, chkbx_activecontact)), false);
                    }
                });
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

                        ClearFields(new ArrayList<>(List.of(auto_email, chkbx_activeemail)), false);
                    }
                });
            }
        });

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

        /*MEMBER ADDRESS LISTENER*/

        auto_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectAddress = (DTownInfo.TownProvince) adapterView.getItemAtPosition(i);

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

                auto_contact.setText(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getSContctNo(), false);
                tie_remarks.setText(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getSRemarksx());
                chkbx_activecontact.setChecked(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getCRecdStat().equals("1"));

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

                auto_email.setText(loSelectEmail.getSEmailAdd(), false);
                chkbx_activecontact.setChecked(loSelectEmail.getCRecdStat().equalsIgnoreCase("1"));
            }
        });

        chkbx_activeemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

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

                String lsFrstNme= tie_firstname.getText() == null || tie_firstname.getText().toString().isEmpty() ? "" : tie_firstname.getText().toString();
                String lsMiddNme= tie_middlename.getText() == null || tie_middlename.getText().toString().isEmpty() ? "" : tie_middlename.getText().toString();
                String lsLastNme= tie_lastname.getText() == null || tie_lastname.getText().toString().isEmpty() ? "" : tie_lastname.getText().toString();
                String lsSuffix= tie_suffix.getText() == null || tie_suffix.getText().toString().isEmpty() ? "" : tie_suffix.getText().toString();

                String lsMemberFullNm = lsFrstNme + " " + lsMiddNme + ". " + lsLastNme + " " + lsSuffix;

                UserAccount.MemberName loMemberNme = new UserAccount.MemberName(
                        lsFrstNme,
                        lsMiddNme,
                        lsLastNme,
                        lsSuffix
                );

                //initialze default values
                EMemberInfo poMember = new EMemberInfo(
                        "",
                        lsSelectLodge,
                        tie_glpid.getText() == null ? "" : tie_glpid.getText().toString(),
                        lsMemberFullNm,
                        String.valueOf(lnSelectCivil),
                        tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString(),
                        String.valueOf(lnSelectStatus),
                        mviewModel.GetCurrentDate(),
                        null,
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
                        String.valueOf(lnSelectStatus)
                );

                //add sponsors
                for (int index = 0; index < paramSponsors.size(); index++){
                    Log.d("Sponsors added ", paramSponsors.get(index));

                    switch (index){

                        case 0:
                            poMember.setSSponsor1(paramSponsors.get(index));
                            break;
                        case 1:
                            poMember.setSSponsor2(paramSponsors.get(index));
                            break;
                        case 2:
                            poMember.setSSponsor3(paramSponsors.get(index));
                            break;
                    }
                }

                int hasActive = 0;
                int hasHomeAddr = 0;

                List<EMemberAddress> laAddressParams = new ArrayList<>();
                for (DTownInfo.TownProvince townProvince : paramTownProvince){
                    Log.d("Address added ", townProvince.getPsTownProvNme());

                    laAddressParams.add(
                            new EMemberAddress(
                                    "",
                                    "",
                                    townProvince.getPsAddressx() + ", " + townProvince.getPsTownProvNme(),
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

                mviewModel.SubmitParameters(loMemberNme, poMember, laAddressParams, laContactParams, laEmailParams, new VM_Member.OnSubmit() {
                    @Override
                    public void OnLoad() {
                        poDialog.ShowDialog("Submitting your information. Please wait . .");
                    }

                    @Override
                    public void OnSuccess() {
                        poDialog.DismissDialog();
                    }

                    @Override
                    public void OnFailed(String fsMesssage) {
                        poDialog.DismissDialog();

                        poMessage.ShowMessage(1, fsMesssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
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
            }
        }
    }

    private boolean isDataValid() {

        if (TextUtils.isEmpty(auto_lodge.getText().toString())) {

            auto_lodge.setError("Lodge is required");
            auto_lodge.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_glpid.getText().toString())) {

            tie_glpid.setError("Account type is required");
            tie_glpid.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_status.getText().toString())) {

            auto_status.setError("Status is required");
            auto_status.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_title.getText().toString())) {

            auto_title.setError("Title is required");
            auto_title.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_lastname.getText().toString())) {

            tie_lastname.setError("Lastname is required");
            tie_lastname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(tie_firstname.getText().toString())) {

            tie_firstname.setError("Firstname is required");
            tie_firstname.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(auto_civil.getText().toString())) {

            auto_civil.setError("Civil status is required");
            auto_civil.requestFocus();
            return false;
        }

        return true;
    }

    public static class LodgeAdapter extends ArrayAdapter<ELodgeInfo>{

        private final Context loContext;
        private final List<ELodgeInfo> lodges;

        public LodgeAdapter(@NonNull Context context, int resource, @NonNull List<ELodgeInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            lodges = objects;
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
            textView.setText(lodges.get(position).getSLodgeNme());

            return view;

        }
    }

    public static class TitleAdapter extends ArrayAdapter<ETitle>{

        private final Context loContext;
        private final List<ETitle> titles;

        public TitleAdapter(@NonNull Context context, int resource, @NonNull List<ETitle> objects) {
            super(context, resource, objects);

            loContext = context;
            titles = objects;
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
            textView.setText(titles.get(position).getSTitleDsc());

            return view;

        }
    }

    public static class MemberAddressAdapter extends ArrayAdapter<DTownInfo.TownProvince>{

        private final Context loContext;
        private final List<DTownInfo.TownProvince> laMemberAddress;
        private List<DTownInfo.TownProvince> laMemberAddressFiltered;

        public MemberAddressAdapter(@NonNull Context context, int resource, @NonNull List<DTownInfo.TownProvince> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberAddress = objects;
            laMemberAddressFiltered = objects;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(laMemberAddressFiltered.get(position).getPsAddressx() + ", " + laMemberAddressFiltered.get(position).getPsTownProvNme());

            return view;

        }

        @Override
        public int getCount() {
            return laMemberAddressFiltered.size();
        }

        @Nullable
        @Override
        public DTownInfo.TownProvince getItem(int position) {
            return laMemberAddressFiltered.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    List<DTownInfo.TownProvince> results = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(laMemberAddress);
                    } else {
                        for (DTownInfo.TownProvince addressInfo : laMemberAddress) {
                            if (addressInfo.getPsTownProvNme().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    addressInfo.getPsAddressx().toLowerCase().contains(constraint.toString().toLowerCase())) {

                                results.add(addressInfo);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    laMemberAddressFiltered = (List<DTownInfo.TownProvince>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public static class MemberContactAdapter extends ArrayAdapter<EMemberContactInfo>{

        private final Context loContext;
        private final List<EMemberContactInfo> laMemberContact;
        private List<EMemberContactInfo> laMemberContactFiltered;

        public MemberContactAdapter(@NonNull Context context, int resource, @NonNull List<EMemberContactInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberContact = objects;
            laMemberContactFiltered = objects;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(laMemberContactFiltered.get(position).getSContctNo());

            return view;

        }

        @Override
        public int getCount() {
            return laMemberContactFiltered.size();
        }

        @Nullable
        @Override
        public EMemberContactInfo getItem(int position) {
            return laMemberContactFiltered.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    List<EMemberContactInfo> results = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(laMemberContact);
                    } else {
                        for (EMemberContactInfo contactInfo : laMemberContact) {
                            if (contactInfo.getSContctNo().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                                    contactInfo.getSRemarksx().toLowerCase().contains(constraint.toString().toLowerCase())) {

                                results.add(contactInfo);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    laMemberContactFiltered = (List<EMemberContactInfo>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

    public static class MemberEmailAdapter extends ArrayAdapter<EMemberEmailInfo>{

        private final Context loContext;
        private final List<EMemberEmailInfo> laMemberEmail;
        private List<EMemberEmailInfo> laMemberEmailFiltered;

        public MemberEmailAdapter(@NonNull Context context, int resource, @NonNull List<EMemberEmailInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberEmail = objects;
            laMemberEmailFiltered = objects;
        }

        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(loContext);
                view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(laMemberEmail.get(position).getSEmailAdd());

            return view;

        }

        @Override
        public int getCount() {
            return laMemberEmailFiltered.size();
        }

        @Nullable
        @Override
        public EMemberEmailInfo getItem(int position) {
            return laMemberEmailFiltered.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    List<EMemberEmailInfo> results = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {
                        results.addAll(laMemberEmail);
                    } else {
                        for (EMemberEmailInfo emailInfo : laMemberEmail) {
                            if (emailInfo.getSEmailAdd().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                results.add(emailInfo);
                            }
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = results;

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    laMemberEmailFiltered = (List<EMemberEmailInfo>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
    }

}