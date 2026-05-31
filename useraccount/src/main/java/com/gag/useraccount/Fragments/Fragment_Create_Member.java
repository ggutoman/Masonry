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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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


    private LinearLayout layout_member,
            layout_personal;

    private MaterialAutoCompleteTextView
            auto_status,
            auto_title,
            auto_sponosr,
            auto_town,
            auto_contact,
            auto_email,
            auto_civil,
            auto_lodge;

    private TextInputLayout til_sponsor,
            til_address,
            til_contact,
            til_email,
            til_midname,
            til_suffix,
            til_civil;

    private ImageButton
            btn_add_address,
            btn_add_contact,
            btn_add_email;

    private MaterialButton btn_add_sponsor, btn_create,btn_cancel;

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix,
            tie_birthdate,
            tie_glpid,
            tie_address,
            tie_remarks;

    private CheckBox chkbx_homeaddr, chkbx_active, chkbx_activecontact, chkbx_activeemail;

    private LodgeAdapter LodgeAdapter;
    private TitleAdapter TitleAdapter;
    private TownCityAdapter TownProvAdapter;
    private MemberAddressAdapter MemberAddressAdapter;
    private MemberContactAdapter MemberContactAdapter;
    private MemberEmailAdapter MemberEmailAdapter;

    private List<String> paramSponsors = new ArrayList<>();
    private List<DTownInfo.TownProvince> paramTownProvince= new ArrayList<>();
    private List<EMemberContactInfo> paramContact= new ArrayList<>();
    private List<EMemberEmailInfo> paramEmail= new ArrayList<>();

    private String lsAddr;
    private String lsTwnIDx;
    private String lsProvIDx;
    private String lsSelectLodge;
    private String lsSelectTitle;

    private int lnSelectCivil;
    private int lnSelectStatus;

    private boolean isHomeAddr;
    private boolean isActive;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_create_member, container, false);

        mviewModel = new ViewModelProvider(this).get(VM_Member.class);
        poMessage = new Message_Dialog(requireActivity());
        poDialog = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poDialog.InitDialog();


        initViews(view);
        initDataReceiver();
        initListeners();

        return view;

    }

    private void initViews(View view) {

        layout_member = view.findViewById(R.id.layout_member);
        layout_personal = view.findViewById(R.id.layout_personal);

        auto_lodge = view.findViewById(R.id.auto_lodge);
        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_title = view.findViewById(R.id.auto_title);
        auto_sponosr = view.findViewById(R.id.auto_sponosr);
        auto_town = view.findViewById(R.id.auto_town);
        auto_contact = view.findViewById(R.id.auto_contact);
        auto_email = view.findViewById(R.id.auto_email);
        auto_civil = view.findViewById(R.id.auto_civil);

        til_sponsor = view.findViewById(R.id.til_sponsor);
        til_address = view.findViewById(R.id.til_town);
        til_contact = view.findViewById(R.id.til_contact);
        til_email = view.findViewById(R.id.til_email);
        til_midname = view.findViewById(R.id.til_midname);
        til_suffix = view.findViewById(R.id.til_suffix);
        til_civil = view.findViewById(R.id.til_civil);
        tie_birthdate = view.findViewById(R.id.tie_birthdate);
        tie_address = view.findViewById(R.id.tie_address);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        btn_add_sponsor = view.findViewById(R.id.btn_add_sponsor);
        btn_add_address = view.findViewById(R.id.btn_add_address);
        btn_add_contact = view.findViewById(R.id.btn_add_contact);
        btn_add_email = view.findViewById(R.id.btn_add_email);
        btn_create = view.findViewById(R.id.btn_create);
        btn_cancel = view.findViewById(R.id.btn_cancel);

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

                LodgeAdapter = new LodgeAdapter(
                        requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item,
                        eLodgeInfos);

                auto_lodge.setAdapter(LodgeAdapter);

                //select first item
                auto_lodge.setSelection(0);

                //enable selection if more than 1 item
                if (eLodgeInfos.size() > 1){
                    auto_lodge.setEnabled(false);
                }else {
                    auto_lodge.setEnabled(true);
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

        mviewModel.TownSearch().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                //display all town list if empty
                if (s.length() < 1){

                    mviewModel.HasNewAddress().observe(requireActivity(), new Observer<List<DTownInfo.TownProvince>>() {
                        @Override
                        public void onChanged(List<DTownInfo.TownProvince> memberAddresses) {

                            paramTownProvince = memberAddresses;

                            MemberAddressAdapter = new MemberAddressAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    memberAddresses
                            );
                            auto_town.setAdapter(MemberAddressAdapter);
                        }
                    });
                    return;
                }

                //display the properties from selected town
                mviewModel.SearchTown(s).observe(requireActivity(), new Observer<List<DTownInfo.TownProvince>>() {
                    @Override
                    public void onChanged(List<DTownInfo.TownProvince> townProvinces) {

                        TownProvAdapter = new TownCityAdapter(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                townProvinces
                        );
                        auto_town.setAdapter(TownProvAdapter);

                    }
                });
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

                if (auto_town.getAdapter() == null){
                    Toast.makeText(requireActivity(), "Selected town is invalid", Toast.LENGTH_SHORT).show();
                    return;
                }

                mviewModel.AddMemberAddress(
                        "",
                        lsTwnIDx,
                        lsProvIDx,
                        auto_town.getText().toString(),
                        tie_address.getText() == null ? "" : tie_address.getText().toString(),
                        chkbx_homeaddr.isChecked() ? "1" : "0",
                        chkbx_active.isChecked() ? "1" : "0"

                );

                ClearFields(new ArrayList<>(List.of(auto_town, tie_address, chkbx_homeaddr, chkbx_active)), false);
            }
        });

        btn_add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.AddMemberContact(
                        "",
                        "",
                        auto_contact.getText() == null ? "" : auto_contact.getText().toString(),
                        tie_remarks.getText() == null ? "" : tie_remarks.getText().toString(),
                        chkbx_activecontact.isChecked() ? "1" : "0"
                );
                ClearFields(new ArrayList<>(List.of(auto_contact, tie_remarks, chkbx_activecontact)), false);
            }
        });

        btn_add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.AddMemberEmail(
                        "",
                        "",
                        auto_email.getText() == null ? "" : auto_email.getText().toString(),
                        chkbx_activecontact.isChecked() ? "1" : "0"
                );
                ClearFields(new ArrayList<>(List.of(auto_email, chkbx_activeemail)), false);
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

        auto_town.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                mviewModel.SearchTownProvince(editable.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                auto_town.showDropDown();
            }
        });

        auto_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get properties to be passed later on
                lsAddr = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsAddressx();
                lsTwnIDx = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsTownIDxx();
                lsProvIDx = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsProvIDxx();
                isHomeAddr = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).isHomeAddr().equals("1");
                isActive = ((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).isActive().equals("1");

                auto_town.setText(((DTownInfo.TownProvince) adapterView.getItemAtPosition(i)).getPsTownProvNme(), false);
                tie_address.setText(lsAddr);
                chkbx_homeaddr.setChecked(isHomeAddr);
                chkbx_active.setChecked(isActive);
            }
        });

        auto_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                auto_contact.setText(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getSContctNo(), false);
                tie_remarks.setText(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getSRemarksx());
                chkbx_activecontact.setChecked(((EMemberContactInfo) adapterView.getItemAtPosition(i)).getCRecdStat().equals("1"));

            }
        });

        auto_email.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                auto_email.setText(((EMemberEmailInfo) adapterView.getItemAtPosition(i)).getSEmailAdd(), false);
                chkbx_activecontact.setChecked(((EMemberEmailInfo) adapterView.getItemAtPosition(i)).getCRecdStat().equals("1"));
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
                }

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
                }

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

    public static class TownCityAdapter extends ArrayAdapter<DTownInfo.TownProvince>{

        private final Context loContext;
        private final List<DTownInfo.TownProvince> towncity;

        public TownCityAdapter(@NonNull Context context, int resource, @NonNull List<DTownInfo.TownProvince> objects) {
            super(context, resource, objects);

            loContext = context;
            towncity = objects;
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
            textView.setText(towncity.get(position).getPsTownProvNme());

            return view;

        }
    }

    public static class MemberAddressAdapter extends ArrayAdapter<DTownInfo.TownProvince>{

        private final Context loContext;
        private final List<DTownInfo.TownProvince> laMemberAddress;

        public MemberAddressAdapter(@NonNull Context context, int resource, @NonNull List<DTownInfo.TownProvince> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberAddress = objects;
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
            textView.setText(laMemberAddress.get(position).getPsAddressx() + ", " + laMemberAddress.get(position).getPsTownProvNme());

            return view;

        }
    }

    public static class MemberContactAdapter extends ArrayAdapter<EMemberContactInfo>{

        private final Context loContext;
        private final List<EMemberContactInfo> laMemberContact;

        public MemberContactAdapter(@NonNull Context context, int resource, @NonNull List<EMemberContactInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberContact = objects;
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
            textView.setText(laMemberContact.get(position).getSContctNo());

            return view;

        }
    }

    public static class MemberEmailAdapter extends ArrayAdapter<EMemberEmailInfo>{

        private final Context loContext;
        private final List<EMemberEmailInfo> laMemberEmail;

        public MemberEmailAdapter(@NonNull Context context, int resource, @NonNull List<EMemberEmailInfo> objects) {
            super(context, resource, objects);

            loContext = context;
            laMemberEmail = objects;
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
    }

}