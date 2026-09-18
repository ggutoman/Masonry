package com.gag.useraccount.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.MemberAddressAdapter;
import org.gag.appdriver.App.Adapters.MemberContactAdapter;
import org.gag.appdriver.App.Adapters.MemberEmailAdapter;
import org.gag.appdriver.App.Core.SMSApp;
import org.gag.appdriver.App.Interface.OnSMSStatus;
import org.gag.appdriver.App.Models.TownProvince;
import org.gag.appdriver.Room.Entities.EMemberAddress;
import org.gag.appdriver.Room.Entities.EMemberContactInfo;
import org.gag.appdriver.Room.Entities.EMemberEmailInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.Disclosure_Dialog;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private TextInputEditText tie_lastname,
            tie_firstname,
            tie_middlename,
            tie_suffix,
            tie_birthdate,
            tie_glpid,
            tie_address,
            tie_remarks,
            tie_intn,
            tie_passing,
            tie_raising,
            tie_suspend;

    private CheckBox chkbx_homeaddr, chkbx_active, chkbx_activecontact, chkbx_activeemail;

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

    private boolean isMemberDataValid(){

        if (lnSelectStatus < 0){
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

    private void initViews(View view) {

        tie_glpid = view.findViewById(R.id.tie_glpid);
        auto_status = view.findViewById(R.id.auto_status);
        auto_town = view.findViewById(R.id.auto_town);
        auto_contact = view.findViewById(R.id.auto_contact);
        auto_email = view.findViewById(R.id.auto_email);
        auto_civil = view.findViewById(R.id.auto_civil);

        tie_birthdate = view.findViewById(R.id.tie_birthdate);
        tie_address = view.findViewById(R.id.tie_address);
        tie_remarks = view.findViewById(R.id.tie_remarks);

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
                tie_intn.setText(loMemberInfo.getDInitiatn());
                tie_passing.setText(loMemberInfo.getDPassedxx());
                tie_raising.setText(loMemberInfo.getDRaisingX());
                tie_suspend.setText(loMemberInfo.getDSuspendx());

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

        tie_intn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();

                new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tie_intn.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        tie_passing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();

                new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tie_passing.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        tie_raising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();

                new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tie_raising.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
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

                //if permission is not granted, redirect user to app settings and manually allow the permission before submitting
                if (requireActivity().checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

                    Disclosure_Dialog poDisclosure = new Disclosure_Dialog(requireActivity());
                    poDisclosure.InitDialog();

                    poDisclosure.ShowDisclosure(org.gag.appdriver.R.drawable.masonry_logo,
                            "SMS permission is required. Please allow before saving your entry", "Deny", "Accept", new Disclosure_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();

                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
                                    startActivity(intent);

                                }
                            });
                    return;
                }

                poMessage.ShowMessage(2, "Do you want to save member information?", "No", "Yes", new Message_Dialog.OnDialogClick() {
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
                                    mviewModel.GetLodgeID(),
                                    tie_glpid.getText() == null ? "" : tie_glpid.getText().toString(),
                                    tie_intn.getText() == null ? "" : tie_intn.getText().toString(),
                                    tie_passing.getText() == null ? "" : tie_passing.getText().toString(),
                                    tie_raising.getText() == null ? "" : tie_raising.getText().toString(),
                                    lsLastNme,
                                    lsFrstNme,
                                    lsMiddNme,
                                    lsSuffix,
                                    String.valueOf(lnSelectCivil),
                                    tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString(),
                                    String.valueOf(lnSelectStatus),
                                    mviewModel.GetCurrentDate(),
                                    tie_suspend.getText() == null ? "" : tie_suspend.getText().toString(),
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

                            loMemberInfo.setSLodgeIDx(mviewModel.GetLodgeID());
                            loMemberInfo.setDInitiatn(tie_intn.getText() == null ? "" : tie_intn.getText().toString());
                            loMemberInfo.setDPassedxx(tie_passing.getText() == null ? "" : tie_passing.getText().toString());
                            loMemberInfo.setDRaisingX(tie_raising.getText() == null ? "" : tie_raising.getText().toString());
                            loMemberInfo.setDSuspendx(tie_suspend.getText() == null ? "" : tie_suspend.getText().toString());
                            loMemberInfo.setSLastName(lsLastNme);
                            loMemberInfo.setSFrstName(lsFrstNme);
                            loMemberInfo.setSMiddName(lsMiddNme);
                            loMemberInfo.setSSuffixNm(lsSuffix);
                            loMemberInfo.setCCvilStat(String.valueOf(lnSelectCivil));
                            loMemberInfo.setDBirthDte(tie_birthdate.getText() == null ? "1900-00-00" : tie_birthdate.getText().toString());
                            loMemberInfo.setCMmbrStat(String.valueOf(lnSelectStatus));
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
                            Log.d("Contacts added ", contactInfo.getSContctNo());

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
                            Log.d("Emails added ", eMemberEmailInfo.getSEmailAdd());

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

                        mviewModel.SubmitParameters(loMemberInfo, laAddressParams, laContactParams, laEmailParams, new VM_Member.OnSubmit() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Submitting your information. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(2, "Member information has been saved successfully. Do you want to send ALSM ID to the member?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                        FinishTask();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        poLoad.ShowDialog("Sending message. Please wait . . .");

                                        //find the primary and active contact number of the member
                                        laContactParams
                                                .stream()
                                                .filter(eMemberContactInfo -> eMemberContactInfo.getCRecdStat().equalsIgnoreCase("1"))
                                                .findAny()
                                                .ifPresent(eMemberContactInfo -> {

                                                    if (loMemberInfo == null) return;

                                                    //compose message for the member's ALSM ID
                                                    String lsMessage = "Kay Gandang Araw, " + loMemberInfo.getSFrstName() + "!\n\n" +
                                                            "Here is your ALSM ID " + loMemberInfo.getSGLPIDNoX() + "\n" +
                                                            "Please do not share your registration ID for Masonry Application";

                                                    new SMSApp(requireActivity(), new OnSMSStatus() {
                                                        @Override
                                                        public void OnResult(String fsResult) {

                                                            poLoad.DismissDialog();
                                                            poMessage.ShowMessage(0, fsResult, "Okay", "", new Message_Dialog.OnDialogClick() {
                                                                @Override
                                                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                                                    poDialog.dismiss();
                                                                    FinishTask();
                                                                }

                                                                @Override
                                                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                                                            });
                                                        }
                                                    }).SendSms(eMemberContactInfo.getSContctNo(), lsMessage);
                                                });

                                    }
                                });
                            }

                            @Override
                            public void OnFailed(String fsMesssage) {
                                poLoad.DismissDialog();

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