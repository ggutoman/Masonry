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
import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Adapters.PositionAdapter;
import org.gag.appdriver.App.Models.LodgeCalendarList;
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

    private String lsStackIDxx;

    private MemberAdapter loMemberAdapter;
    private PositionAdapter loPositionAdapter;

    private EOfficer loOfficer;
    private EMemberInfo loSelectMember;
    private EPosition loSelectPosition;

    private String lsSelectMember, lsSelectCalendar;
    private int lnSelectLevel = -1, lnSelectStatus = -1;

    private TextInputEditText tie_remarks;
    private MaterialAutoCompleteTextView auto_member, auto_position, auto_type, auto_status;
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

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){
        auto_member = view.findViewById(R.id.auto_member);
        auto_position = view.findViewById(R.id.auto_position);
        auto_type = view.findViewById(R.id.auto_type);
        auto_status = view.findViewById(R.id.auto_status);
        tie_remarks = view.findViewById(R.id.tie_remarks);

        btn_create = view.findViewById(R.id.btn_create);
    }

    private void InitDataReceiver(){

        //initialize defualt values
        lsSelectMember = getArguments() == null || !getArguments().containsKey("fsMemberID") ? "" : getArguments().getString("fsMemberID");
        lsSelectCalendar = getArguments() == null || !getArguments().containsKey("fsYearID") ? "" : getArguments().getString("fsYearID");

        /** 1.GET OFFICER INFORMATION **/
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
                                        .popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                        return;
                    }

                }else {
                    loOfficer = eOfficer;
                }

                /** 2.GET MASONIC CALENDAR INFORMATION **/
                mViewmodel.GetLodgeCalendar().observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        for (LodgeCalendarList loCalendar : lodgeCalendarLists){

                            //get masonic calendar for the year, only if no arguments is passed
                            if (loCalendar.getNYearxxxx().equalsIgnoreCase(mViewmodel.GetFormattedDate(mViewmodel.GetCurrentDate(), "yyyy"))){
                                if(getArguments() == null) lsSelectCalendar = loCalendar.getSYearIDxx();
                            }
                        }
                    }
                });

                /** 3.GET MEMBER/S INFORMATION **/
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

                                    //if member information is found the do not to change selection of member
                                    auto_member.setEnabled(false);
                                });
                    }
                });

                /** 4.GET POSITION LIST INFORMATION **/
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

        auto_position.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectPosition = (EPosition) adapterView.getItemAtPosition(i);
                auto_position.setText(loSelectPosition.getSPositnDs());
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

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!IsEntryOkay()) return;

                poMessage.ShowMessage(2, "Assigning this member will remove the current officer from the role. Do you want to continue?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        EOfficer loParam = new EOfficer(
                                lsSelectCalendar,
                                loSelectPosition.getSPositnCd(),
                                loSelectMember.getSMemberID(),
                                String.valueOf(lnSelectLevel),
                                String.valueOf(lnSelectStatus),
                                mViewmodel.GetUserID() == null ? "" : mViewmodel.GetUserID(),
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
                                                .popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

        if (lsSelectCalendar == null){
            Toast.makeText(requireActivity(), "Masonic calendar not set, Download parameters to refresh your data", Toast.LENGTH_SHORT).show();
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