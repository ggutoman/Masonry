package com.gag.useraccount.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.MemberAddressAdapter;
import org.gag.appdriver.App.Adapters.MemberContactAdapter;
import org.gag.appdriver.App.Adapters.MemberEmailAdapter;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Fragment_Member extends Fragment{

    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private VM_Member mviewModel;

    private MaterialAutoCompleteTextView
            auto_status,
            auto_town,
            auto_contact,
            auto_email,
            auto_civil;

    private ImageButton
            btn_add_address,
            btn_add_contact,
            btn_add_email;

    private MaterialButton btn_create, btn_save_address, btn_save_contact, btn_save_email;
    private MaterialTextView mtv_generate, mtv_resend, mtv_alsm;

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix,
            tie_birthdate,
            tie_glpid,
            tie_address,
            tie_remarks,
            tie_dMbr,
            tie_intn,
            tie_passing,
            tie_raising,
            tie_suspend;

    private CheckBox chkbx_homeaddr, chkbx_active, chkbx_activecontact, chkbx_activeemail;

    private ImageButton btn_download;

    private Dialog_Add_Member_Info poDialogAddMember;
    private MemberAddressAdapter MemberAddressAdapter;
    private MemberContactAdapter MemberContactAdapter;
    private MemberEmailAdapter MemberEmailAdapter;

    private String lsStackIDxx;

    private List<TownProvince> paramTownProvince= new ArrayList<>();
    private List<EMemberContactInfo> paramContact= new ArrayList<>();
    private List<EMemberEmailInfo> paramEmail= new ArrayList<>();

    private EMemberInfo loMemberInfo;
    private TownProvince loSelectAddress;
    private EMemberContactInfo loSelectContact;
    private EMemberEmailInfo loSelectEmail;

    private interface OnDate{
        void OnSelect(String fsDate);
    }

    private int lnSelectCivil = -1, lnSelectStatus = -1, lnSelectAddress = -1, lnSelectContact = -1, lnSelectEmail = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_member, container, false);

        mviewModel = new ViewModelProvider(this).get(VM_Member.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());
        poDialogAddMember = new Dialog_Add_Member_Info(requireActivity(), mviewModel, getViewLifecycleOwner());

        poMessage.InitDialog();
        poLoad.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        initViews(view);
        initDataReceiver();
        initListeners();

        return view;

    }

    private void initViews(View view) {

        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_town = view.findViewById(R.id.auto_town);
        auto_contact = view.findViewById(R.id.auto_contact);
        auto_email = view.findViewById(R.id.auto_email);
        auto_civil = view.findViewById(R.id.auto_civil);

        mtv_alsm = view.findViewById(R.id.mtv_alsm);
        mtv_generate = view.findViewById(R.id.mtv_generate);
        mtv_resend = view.findViewById(R.id.mtv_resend);

        tie_birthdate = view.findViewById(R.id.tie_birthdate);
        tie_address = view.findViewById(R.id.tie_address);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        tie_dMbr = view.findViewById(R.id.tie_dMbr);
        tie_intn = view.findViewById(R.id.tie_intn);
        tie_passing = view.findViewById(R.id.tie_passing);
        tie_raising = view.findViewById(R.id.tie_raising);
        tie_suspend = view.findViewById(R.id.tie_suspend);

        btn_add_address = view.findViewById(R.id.btn_add_address);
        btn_add_contact = view.findViewById(R.id.btn_add_contact);
        btn_add_email = view.findViewById(R.id.btn_add_email);
        btn_create = view.findViewById(R.id.btn_create);
        btn_save_address = view.findViewById(R.id.btn_save_address);
        btn_save_contact = view.findViewById(R.id.btn_save_contact);
        btn_save_email = view.findViewById(R.id.btn_save_email);
        btn_download = view.findViewById(R.id.btn_download);

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

        mtv_resend.setVisibility(lsStackIDxx.equalsIgnoreCase("create_member") ? View.GONE : View.VISIBLE);
        btn_create.setText(lsStackIDxx.equalsIgnoreCase("create_member") ? "Create Member" : "Update Member");

        //if viewing of member data, check id to load if initialized properly
        if (lsStackIDxx.equalsIgnoreCase("view_members")){

            if (getArguments() == null || !getArguments().containsKey("fsGLPIDxx")){

                poMessage.ShowMessage(1, "ALSM ID is not initialized properly", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                        requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
                return;
            }
        }

        //load member information
        mviewModel.ObserveMemberInfo(getArguments() == null ? "" : getArguments().getString("fsGLPIDxx")).observe(getViewLifecycleOwner(), new Observer<EMemberInfo>() {
            @Override
            public void onChanged(EMemberInfo eMemberInfo) {

                if (eMemberInfo == null){

                    loMemberInfo = new EMemberInfo(
                            "",
                            mviewModel.GetLodgeID(),
                            "",
                            null,
                            null,
                            null,
                            "",
                            "",
                            "",
                            "",
                            "0",
                            null,
                            "0",
                            mviewModel.GetCurrentDate(),
                            null,
                            null,
                            lnSelectStatus != 1 ? "0" : "1"
                    );
                    btn_download.setVisibility(View.GONE);

                }else {

                    loMemberInfo = eMemberInfo;
                    mviewModel.DownloadMemberInfo(loMemberInfo.getSMemberID(), new VM_Member.OnDownload() {
                        @Override
                        public void Loading() {
                            Toast.makeText(requireActivity(), "Downloading member information...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void Finished(String fsMessage) {
                            Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                    btn_download.setVisibility(View.VISIBLE);
                }

                //initialize member name and birthdate
                tie_glpid.setText(loMemberInfo.getSGLPIDNoX());
                tie_lastname.setText(loMemberInfo.getSLastName());
                tie_firstname.setText(loMemberInfo.getSFrstName());
                tie_middlename.setText(loMemberInfo.getSMiddName());
                tie_suffix.setText(loMemberInfo.getSSuffixNm());
                tie_birthdate.setText(loMemberInfo.getDBirthDte());
                tie_dMbr.setText(loMemberInfo.getDMembrshp());
                tie_intn.setText(loMemberInfo.getDInitiatn());
                tie_passing.setText(loMemberInfo.getDPassedxx());
                tie_raising.setText(loMemberInfo.getDRaisingX());
                tie_suspend.setText(loMemberInfo.getDSuspendx());

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

                auto_status.setText(mviewModel.GetAccountStatus().get(Integer.parseInt(loMemberInfo.getCMmbrStat() == null ? "0" : loMemberInfo.getCMmbrStat())), false);
                lnSelectStatus = Integer.parseInt(loMemberInfo.getCMmbrStat());

                auto_civil.setText(mviewModel.GetCivilStatus().get(Integer.parseInt(loMemberInfo.getCCvilStat() == null ? "0" : loMemberInfo.getCCvilStat())), false);
                lnSelectCivil = Integer.parseInt(loMemberInfo.getCCvilStat());
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

                lnSelectAddress = memberAddresses.size() - 1;
                if (lnSelectAddress < 0) return;

                loSelectAddress = memberAddresses.get(lnSelectAddress);

                //display added data
                auto_town.setText(loSelectAddress.getPsTownProvNme(), false);
                tie_address.setText(loSelectAddress.getPsAddressx());
                chkbx_homeaddr.setChecked(loSelectAddress.isHomeAddr().equalsIgnoreCase("1"));
                chkbx_active.setChecked(loSelectAddress.isActive().equalsIgnoreCase("1"));
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

                lnSelectContact = eMemberContactInfos.size() -1;
                if (lnSelectContact < 0) return;

                loSelectContact = eMemberContactInfos.get(lnSelectContact);

                //display added data
                auto_contact.setText(loSelectContact.getSContctNo(), false);
                tie_remarks.setText(loSelectContact.getSRemarksx());
                chkbx_activecontact.setChecked(loSelectContact.getCRecdStat().equalsIgnoreCase("1"));
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

                lnSelectEmail = eMemberEmailInfos.size() - 1;
                if (lnSelectEmail < 0) return;

                loSelectEmail = eMemberEmailInfos.get(lnSelectEmail);

                //display added data
                auto_email.setText(loSelectEmail.getSEmailAdd(), false);
                chkbx_activeemail.setChecked(loSelectEmail.getCRecdStat().equalsIgnoreCase("1"));
            }
        });
    }

    private void initListeners() {

        tie_glpid.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mtv_alsm.setVisibility(charSequence.toString().equalsIgnoreCase(loMemberInfo.getSGLPIDNoX()) ? View.GONE : View.VISIBLE);
            }
        });

        tie_dMbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDatePicker("Select Date of Membership", new OnDate() {
                    @Override
                    public void OnSelect(String fsDate) {
                        tie_dMbr.setText(fsDate);
                    }
                });
            }
        });

        tie_intn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDatePicker("Select Date of Initiation", new OnDate() {
                    @Override
                    public void OnSelect(String fsDate) {
                        tie_intn.setText(fsDate);
                    }
                });
            }
        });

        tie_passing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDatePicker("Select Date of Passing", new OnDate() {
                    @Override
                    public void OnSelect(String fsDate) {
                        tie_passing.setText(fsDate);
                    }
                });
            }
        });

        tie_raising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDatePicker("Select Date of Raising", new OnDate() {
                    @Override
                    public void OnSelect(String fsDate) {
                        tie_raising.setText(fsDate);
                    }
                });
            }
        });

        tie_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDatePicker("Select Date of Birthdate", new OnDate() {
                    @Override
                    public void OnSelect(String fsDate) {
                        tie_birthdate.setText(fsDate);
                    }
                });
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

        btn_download.setOnClickListener(view -> {

            poMessage.ShowMessage(2, "Downloading information will refresh the current data. Cpntinue?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                @Override
                public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                @Override
                public void OnNegative(@NotNull AlertDialog poDialog) {
                    poDialog.dismiss();

                    mviewModel.DownloadMemberInfo(loMemberInfo == null ? "" : loMemberInfo.getSMemberID(), new VM_Member.OnDownload() {
                        @Override
                        public void Loading() {
                            Toast.makeText(requireActivity(), "Downloading member information...", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void Finished(String fsMessage) {
                            Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_LONG).show();
                            initDataReceiver();
                        }
                    });

                }
            });
        });

        /*MEMBER ADDRESS LISTENER*/
        auto_town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectAddress = paramTownProvince.indexOf((TownProvince) adapterView.getItemAtPosition(i));
                loSelectAddress = paramTownProvince.get(lnSelectAddress);

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

                lnSelectContact = paramContact.indexOf((EMemberContactInfo) adapterView.getItemAtPosition(i));
                loSelectContact = paramContact.get(lnSelectContact);

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
                if (!charSequence.toString().equalsIgnoreCase(loSelectContact.getSRemarksx() == null ? "" : loSelectContact.getSRemarksx())){
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

                        //do not allow same contact
                        AtomicBoolean hasContact = new AtomicBoolean(false);
                        paramContact.forEach(eMemberContactInfo -> {
                            if (eMemberContactInfo.getSContctNo().equalsIgnoreCase(lsContactNo)){
                                hasContact.set(true);
                            }
                        });

                        if (hasContact.get()){
                            poMessage.ShowMessage(1, "Contact number already exists", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        mviewModel.VerifyContact(lsContactNo, new VM_Member.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Verifying contact. Please wait . . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                mviewModel.AddMemberContact(
                                        "",
                                        "",
                                        lsContactNo,
                                        lsRemarks,
                                        lsActive
                                );

                            }

                            @Override
                            public void OnFailed(String fsMesssage) {
                                poLoad.DismissDialog();

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

                lnSelectEmail = paramEmail.indexOf((EMemberEmailInfo) adapterView.getItemAtPosition(i));
                loSelectEmail = paramEmail.get(lnSelectEmail);

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

                        //do not allow same email
                        AtomicBoolean hasEmail = new AtomicBoolean(false);
                        paramEmail.forEach(eMemberEmailInfo -> {
                            if (eMemberEmailInfo.getSEmailAdd().equalsIgnoreCase(lsEmail)){
                                hasEmail.set(true);
                            }
                        });

                        if (hasEmail.get()){
                            poMessage.ShowMessage(1, "Email address already exists", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                            return;
                        }

                        mviewModel.VerifyEmail(lsEmail, new VM_Member.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Verifying email. Please wait . . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                mviewModel.AddMemberEmail(
                                        "",
                                        "",
                                        lsEmail,
                                        lsActive
                                );
                            }

                            @Override
                            public void OnFailed(String fsMesssage) {
                                poLoad.DismissDialog();

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

        /*SAVE LISTENERS*/
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //validate member data information
                if (!isMemberDataValid()) return;

                poMessage.ShowMessage(2, "Do you want to save member information?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poConfirmDialog) {
                        poConfirmDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poConfirmDialog) {

                        poConfirmDialog.dismiss();

                        //member information
                        loMemberInfo.setDMembrshp(tie_dMbr.getText() == null ? "" : tie_dMbr.getText().toString());
                        loMemberInfo.setDInitiatn(tie_intn.getText() == null ? "" : tie_intn.getText().toString());
                        loMemberInfo.setDPassedxx(tie_passing.getText() == null ? "" : tie_passing.getText().toString());
                        loMemberInfo.setDRaisingX(tie_raising.getText() == null ? "" : tie_raising.getText().toString());
                        loMemberInfo.setDMembrshp(tie_dMbr.getText() == null ? "" : tie_dMbr.getText().toString());
                        loMemberInfo.setCMmbrStat(String.valueOf(lnSelectStatus));

                        //personal information
                        loMemberInfo.setSLastName(tie_lastname.getText() == null || tie_lastname.getText().toString().isEmpty() ? "" : tie_lastname.getText().toString());
                        loMemberInfo.setSFrstName(tie_firstname.getText() == null || tie_firstname.getText().toString().isEmpty() ? "" : tie_firstname.getText().toString());
                        loMemberInfo.setSMiddName(tie_middlename.getText() == null || tie_middlename.getText().toString().isEmpty() ? "" : tie_middlename.getText().toString());
                        loMemberInfo.setSSuffixNm(tie_suffix.getText() == null || tie_suffix.getText().toString().isEmpty() ? "" : tie_suffix.getText().toString());
                        loMemberInfo.setCCvilStat(String.valueOf(lnSelectCivil));
                        loMemberInfo.setDBirthDte(tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString());

                        int hasActive = 0;
                        int hasHomeAddr = 0;

                        List<EMemberAddress> laAddressParams = new ArrayList<>();
                        for (TownProvince townProvince : paramTownProvince){

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
                                            mviewModel.GetUserID() == null ? "" : mviewModel.GetUserID(),
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

                            laContactParams.add(
                                    new EMemberContactInfo(
                                            contactInfo.getSContctID(),
                                            contactInfo.getSMemberID(),
                                            contactInfo.getSContctNo(),
                                            contactInfo.getSRemarksx(),
                                            contactInfo.getCRecdStat(),
                                            mviewModel.GetUserID() == null ? "" : mviewModel.GetUserID(),
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

                            laEmailParams.add(
                                    new EMemberEmailInfo(
                                            eMemberEmailInfo.getSMailIDxx(),
                                            eMemberEmailInfo.getSMemberID(),
                                            eMemberEmailInfo.getSEmailAdd(),
                                            eMemberEmailInfo.getCRecdStat(),
                                            mviewModel.GetUserID() == null ? "" : mviewModel.GetUserID(),
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
                        //compose message for the member's ALSM ID before saving
                        String lsMessage = "Good day Bro " + loMemberInfo.getSFrstName() + "!\n\n" +
                                "Your ALSM ID is " + loMemberInfo.getSGLPIDNoX() + "\n" +
                                "Use this code to create an account in ALSM Portal application.";

                        mviewModel.SubmitParameters(loMemberInfo, laAddressParams, laContactParams, laEmailParams, new VM_Member.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Saving member information. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(0, "Member information has been saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        /* send ALSM ID only to new member's primary contact number after saving member */
                                        if (lsStackIDxx.equalsIgnoreCase("create_member")){

                                            for (EMemberContactInfo contactInfo : laContactParams){

                                                if (contactInfo.getCRecdStat().equalsIgnoreCase("1")){

                                                    mviewModel.SendSms(contactInfo.getSContctNo(), lsMessage, new VM_Member.OnSubmit() {
                                                        @Override
                                                        public void OnLoad() {
                                                            poLoad.ShowDialog("Sending ALSM ID to member. Please wait . . .");
                                                        }

                                                        @Override
                                                        public void OnSuccess() {
                                                            poLoad.DismissDialog();
                                                            FinishTask();
                                                        }

                                                        @Override
                                                        public void OnFailed(String fsMesssage) {
                                                            poLoad.DismissDialog();

                                                            poMessage.ShowMessage(0, fsMesssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                                                @Override
                                                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                                                    poDialog.dismiss();
                                                                    FinishTask();
                                                                }

                                                                @Override
                                                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                                                            });
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                            return;
                                        }
                                        FinishTask();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                                });
                            }

                            @Override
                            public void OnFailed(String fsMesssage) {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(1, fsMesssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                        FinishTask();
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

        mtv_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mviewModel.GenerateALSMID("", new VM_Member.OnGetALSMID() {
                    @Override
                    public void onLoad() {
                        poLoad.ShowDialog("Generating ID. Please wait . . .");
                    }

                    @Override
                    public void onSuccess(String fsID) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), "ALSM ID generated successfully", Toast.LENGTH_SHORT).show();

                        loMemberInfo.setSGLPIDNoX(fsID);
                        tie_glpid.setText(loMemberInfo.getSGLPIDNoX());
                    }

                    @Override
                    public void onError(String fsError) {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                                tie_glpid.setText(loMemberInfo.getSGLPIDNoX());
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                    }
                });
            }
        });

        mtv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poMessage.ShowMessage(2, " Do you want to resend ALSM ID to the member?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        String ls_receiver = "";
                        for (EMemberContactInfo loContact: paramContact) {

                            if (loContact.getCRecdStat().equalsIgnoreCase("1")) {
                                ls_receiver = loContact.getSContctNo();
                                break;
                            }
                        }

                        if (!ls_receiver.isEmpty()){

                            //compose message for the member's ALSM ID
                            String lsMessage = "Good day Bro. " + loMemberInfo.getSFrstName() + "!\n\n" +
                                    "Your ALSM ID is " + loMemberInfo.getSGLPIDNoX() + "\n" +
                                    "Use this code to create an account in ALSM Portal application.";

                            mviewModel.SendSms(ls_receiver, lsMessage, new VM_Member.OnSubmit() {
                                @Override
                                public void OnLoad() {
                                    poLoad.ShowDialog("Sending ALSM ID to member. Please wait . . .");
                                }

                                @Override
                                public void OnSuccess() {
                                    poLoad.DismissDialog();

                                    poMessage.ShowMessage(0, "ALSM ID has been sent successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                                    });
                                }

                                @Override
                                public void OnFailed(String fsMesssage) {
                                    poLoad.DismissDialog();

                                    poMessage.ShowMessage(0, fsMesssage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                                    });
                                }
                            });

                        }else {

                        }
                    }
                });

            }
        });

        mtv_alsm.setOnClickListener(view -> {

            mviewModel.GenerateALSMID(tie_glpid.getText() == null ? "" : tie_glpid.getText().toString(), new VM_Member.OnGetALSMID() {
                @Override
                public void onLoad() {
                    poLoad.ShowDialog("Verifying ALSM ID. Please wait . . .");
                }

                @Override
                public void onSuccess(String fsID) {
                    poLoad.DismissDialog();
                    Toast.makeText(requireActivity(), "ALSM ID successfully assigned", Toast.LENGTH_SHORT).show();

                    loMemberInfo.setSGLPIDNoX(fsID);
                    tie_glpid.setText(loMemberInfo.getSGLPIDNoX());
                }

                @Override
                public void onError(String fsError) {
                    poLoad.DismissDialog();

                    poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                            tie_glpid.setText(loMemberInfo.getSGLPIDNoX());
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {}
                    });
                }
            });
        });
    }

    private boolean isMemberDataValid(){

        if (tie_glpid.getText() == null || tie_glpid.getText().toString().isEmpty()){
            Toast.makeText(requireActivity(), "Please enter ALSM ID", Toast.LENGTH_SHORT).show();
            auto_status.requestFocus();
            return false;
        }else if (tie_dMbr.getText() == null || tie_dMbr.getText().toString().isEmpty()){
            Toast.makeText(requireActivity(), "Please select date of membership", Toast.LENGTH_SHORT).show();
            auto_status.requestFocus();
            return false;
        }else if (lnSelectStatus < 0){
            Toast.makeText(requireActivity(), "Please select account status", Toast.LENGTH_SHORT).show();
            auto_status.requestFocus();
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

    private void ShowDatePicker(String fsTitle, OnDate foCallback){

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                .datePicker()
                .setTitleText(fsTitle)
                .build();

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                foCallback.OnSelect(mviewModel.GetFormattedDate(selection));
            }
        });

        picker.show(getParentFragmentManager(), "DATE_PICKER");

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

    private void FinishTask(){

        //reset only text from selection
        ClearFields(new ArrayList<>(List.of(auto_status, auto_civil)), false);

        //clear all fields and adapters
        ClearFields(new ArrayList<>(List.of(tie_glpid, tie_lastname,
                        tie_firstname, tie_middlename, tie_suffix, tie_birthdate, tie_address,
                        auto_town, chkbx_homeaddr, chkbx_homeaddr, auto_contact, tie_remarks, chkbx_activecontact,
                        auto_email, chkbx_activeemail, btn_save_address, btn_save_contact, btn_save_email
                )
                ),
                true);

        //reset parameters or data
        loMemberInfo = null;
        paramTownProvince = null;
        paramContact = null;
        paramEmail = null;

        mviewModel.ClearSponsor();
        mviewModel.ClearAddress();
        mviewModel.ClearContacts();
        mviewModel.ClearEmails();

        Activity activity = getActivity();
        if (activity != null) {

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

    }
}