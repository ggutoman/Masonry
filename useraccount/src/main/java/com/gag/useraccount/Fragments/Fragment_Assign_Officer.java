package com.gag.useraccount.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.useraccount.R;
import com.gag.useraccount.ViewModel.VM_Account;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Adapters.PositionAdapter;
import org.gag.appdriver.Room.DataObject.DLodgeCalendar;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EOfficer;
import org.gag.appdriver.Room.Entities.EPosition;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Assign_Officer extends Fragment {

    private VM_Member mViewmodel;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;

    private LodgeCalendarAdapter loLodgeCalAdapter;
    private MemberAdapter loMemberAdapter;
    private PositionAdapter loPositionAdapter;

    private EOfficer loOfficer;
    private DLodgeCalendar.LodgeCalendarList loSelectCalendar;
    private EMemberInfo loSelectMember;
    private EPosition loSelectPosition;

    private String lsSelectMember, lsSelectCalendar;
    private int lnSelectLevel = -1, lnSelectStatus = -1;

    private MaterialTextView mtv_remarks;
    private TextInputLayout til_remarks;
    private TextInputEditText tie_remarks;
    private MaterialAutoCompleteTextView  tie_yearid, auto_member, auto_position, auto_type, auto_status;
    private MaterialButton btn_create;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_assign_officer, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Member.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoading = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoading.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        tie_yearid = view.findViewById(R.id.tie_yearid);
        auto_member = view.findViewById(R.id.auto_member);
        auto_position = view.findViewById(R.id.auto_position);
        auto_type = view.findViewById(R.id.auto_type);
        auto_status = view.findViewById(R.id.auto_status);

        mtv_remarks = view.findViewById(R.id.mtv_remarks);
        til_remarks = view.findViewById(R.id.til_remarks);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        btn_create = view.findViewById(R.id.btn_create);
    }

    private void InitDataReceiver(){

        if (getArguments() == null){
            btn_create.setText("Assign Officer");
            lsSelectMember = "";
            lsSelectCalendar = "";
        }else {

            if ((getArguments().getString("fsMemberID") == null || getArguments().getString("fsMemberID").isEmpty()) ||
                    getArguments().getString("fsYearID") == null || getArguments().getString("fsYearID").isEmpty()){

                poMessage.ShowMessage(1, "Could not verify officer data", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .remove(Fragment_Assign_Officer.this)
                                .commit();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
                return;
            }
            btn_create.setText("Update Officer");

            lsSelectMember = getArguments().getString("fsMemberID");
            lsSelectCalendar = getArguments().getString("fsYearID");
        }

        mViewmodel.ObserveOfficerInfo(lsSelectMember, lsSelectCalendar).observe(getViewLifecycleOwner(), new Observer<EOfficer>() {
            @Override
            public void onChanged(EOfficer eOfficer) {

                //do not proceed if member information is not found
                if (eOfficer == null){

                    //if member information is not found via argument's passed GLPID (update member only), validate and return
                    if (getArguments() != null){

                        poMessage.ShowMessage(1, "Could not load officer information", "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                requireActivity()
                                        .getSupportFragmentManager()
                                        .beginTransaction()
                                        .remove(Fragment_Assign_Officer.this)
                                        .commit();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                        return;
                    }

                    //hide remarks field
                    mtv_remarks.setVisibility(View.GONE);
                    til_remarks.setVisibility(View.GONE);
                    tie_remarks.setVisibility(View.GONE);
                }else {

                    loOfficer = eOfficer;

                    //display remarks field
                    mtv_remarks.setVisibility(View.VISIBLE);
                    til_remarks.setVisibility(View.VISIBLE);
                    tie_remarks.setVisibility(View.VISIBLE);
                }

                mViewmodel.GetLodgeCalendar().observe(getViewLifecycleOwner(), new Observer<List<DLodgeCalendar.LodgeCalendarList>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(List<DLodgeCalendar.LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        loLodgeCalAdapter = new LodgeCalendarAdapter(
                                requireActivity(),
                                org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                                lodgeCalendarLists
                        );

                        tie_yearid.setAdapter(loLodgeCalAdapter);

                        if (loOfficer == null) return;

                        lodgeCalendarLists.stream()
                                .filter(loCalendar -> loCalendar.getSYearIDxx()
                                        .equalsIgnoreCase(loOfficer.getSYearIDxx()))
                                .findFirst()
                                .ifPresent(loCalendar ->{

                                    tie_yearid.setText(loCalendar.getSLodgeNme() + " (" + loCalendar.getNYearxxxx() + ")", false);
                                    loSelectCalendar = loCalendar;

                                });

                    }
                });

                mViewmodel.ObserverMemberList().observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(List<EMemberInfo> eMemberInfos) {

                        if (eMemberInfos == null) return;

                        loMemberAdapter = new MemberAdapter(requireActivity(),
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                eMemberInfos);

                        auto_member.setAdapter(loMemberAdapter);

                        if (loOfficer == null) return;
                        eMemberInfos.stream()
                                .filter(loMember -> loMember.getSMemberID()
                                        .equalsIgnoreCase(loOfficer.getSMemberID()))
                                .findFirst()
                                .ifPresent(loMember ->{

                                    auto_member.setText(loMember.getSFrstName() + " " + loMember.getSLastName(), false);
                                    loSelectMember = loMember;
                                });
                    }
                });

                mViewmodel.ObserverPositionList().observe(getViewLifecycleOwner(), new Observer<List<EPosition>>() {
                    @Override
                    public void onChanged(List<EPosition> ePositions) {

                        if (ePositions == null) return;

                        loPositionAdapter = new PositionAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ePositions);
                        auto_position.setAdapter(loPositionAdapter);

                        if (loOfficer == null) return;
                        ePositions.stream()
                                .filter(loPosition -> loPosition.getSPositnCd()
                                        .equalsIgnoreCase(loOfficer.getSPositnCd()))
                                .findFirst()
                                .ifPresent(loPosition -> {

                                    auto_position.setText(loPosition.getSPositnDs(), false);
                                    loSelectPosition = loPosition;
                                });
                    }
                });

                auto_type.setAdapter(
                        new ArrayAdapter<>(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                mViewmodel.GetOfficerTypes()
                        ));

                auto_status.setAdapter(
                        new ArrayAdapter<>(
                                requireActivity(),
                                android.R.layout.simple_spinner_dropdown_item,
                                mViewmodel.GetOfficerStatus()
                        ));

                if (loOfficer == null) return;
                lnSelectLevel = loOfficer.getCAppointx() == null ? 0 : Integer.parseInt(loOfficer.getCAppointx());
                auto_type.setText(mViewmodel.GetOfficerTypes().get(loOfficer.getCAppointx() == null ? 0 : Integer.parseInt(loOfficer.getCAppointx())), false);

                lnSelectStatus = loOfficer.getCStatusxx() == null ? 0 : Integer.parseInt(loOfficer.getCStatusxx());
                auto_status.setText(mViewmodel.GetOfficerStatus().get(loOfficer.getCStatusxx() == null ? 0 : Integer.parseInt(loOfficer.getCStatusxx())), false);
            }
        });
    }

    private void InitListener(){

        tie_yearid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectCalendar= (DLodgeCalendar.LodgeCalendarList) adapterView.getItemAtPosition(i);
                tie_yearid.setText(loSelectCalendar.getSLodgeNme() + " (" + loSelectCalendar.getNYearxxxx() + ")");

                lsSelectCalendar = loSelectCalendar.getSYearIDxx();
            }
        });

        auto_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (loMemberAdapter == null) return;

                loSelectMember = (EMemberInfo) adapterView.getItemAtPosition(i);
                if (loSelectMember == null) return;

                String lsMiddlNme = loSelectMember.getSMiddName() == null ? "" : loSelectMember.getSMiddName();
                String lsSuffix = loSelectMember.getSSuffixNm() == null ? "" : loSelectMember.getSSuffixNm();

                String lsMemberNm = loSelectMember.getSFrstName() + " " +
                        lsMiddlNme + " " +
                        loSelectMember.getSLastName() + " " +
                        lsSuffix;

                auto_member.setText(lsMemberNm);

                lsSelectMember = loSelectMember.getSMemberID();
            }
        });

        auto_position.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectPosition = (EPosition) adapterView.getItemAtPosition(i);
                auto_position.setText(loSelectPosition.getSPositnDs());
            }
        });

        auto_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lnSelectLevel = i;
            }
        });

        auto_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lnSelectStatus = i;
            }
        });

        tie_yearid.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loLodgeCalAdapter == null) return;
                loLodgeCalAdapter.getFilter().filter(charSequence.toString());
            }
        });

        auto_member.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loMemberAdapter == null) return;
                loMemberAdapter.getFilter().filter(charSequence.toString());
            }
        });

        auto_position.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loPositionAdapter == null) return;
                loPositionAdapter.getFilter().filter(charSequence.toString());
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!IsEntryOkay()) return;

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        EOfficer loParam = new EOfficer(
                                loSelectCalendar.getSYearIDxx(),
                                0,
                                loSelectPosition.getSPositnCd(),
                                loSelectMember.getSMemberID(),
                                String.valueOf(lnSelectLevel),
                                String.valueOf(lnSelectStatus),
                                mViewmodel.GetUserID(),
                                mViewmodel.GetCurrentDate(),
                                mViewmodel.GetCurrentDateTime()
                        );

                        mViewmodel.AssignOfficer(loParam, tie_remarks.getText() == null ? "" : tie_remarks.getText().toString(), new VM_Account.OnSubmit() {
                            @Override
                            public void onLoad() {
                                poLoading.ShowDialog("Submitting changes. Please wait , , ,");
                            }

                            @Override
                            public void onSuccess() {
                                poLoading.DismissDialog();

                                poMessage.ShowMessage(0, "Officer is assigned successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        requireActivity().getSupportFragmentManager()
                                                .popBackStack("assign_officer", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onError(String fsError) {
                                poLoading.DismissDialog();

                                poMessage.ShowMessage(1, fsError, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    private boolean IsEntryOkay(){

        if (loSelectCalendar == null){
            Toast.makeText(requireActivity(), "Please select a calendar lodge", Toast.LENGTH_SHORT).show();
            return false;
        }else if (loSelectMember == null){
            Toast.makeText(requireActivity(), "Please select a member", Toast.LENGTH_SHORT).show();
            return false;
        }else if (loSelectPosition == null){
            Toast.makeText(requireActivity(), "Please select a position", Toast.LENGTH_SHORT).show();
            return false;
        }else if (lnSelectLevel < 0){
            Toast.makeText(requireActivity(), "Please select a officer level", Toast.LENGTH_SHORT).show();
            return false;
        }else if (lnSelectStatus < 0){
            Toast.makeText(requireActivity(), "Please select a officer status", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}