package com.gag.accounting.Disbursement.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gag.accounting.Disbursement.ViewModel.VM_Funds;
import com.gag.accounting.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.EFundTurnOver;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Turnover_Funds extends Fragment {

    private LoadDialog poLoading;
    private Message_Dialog poMessage;

    private VM_Funds mViewmodel;
    private LodgeCalendarAdapter loLodgeCalAdapter;
    private LodgeCalendarList loSelectedCal;
    private EFundTurnOver loTurnover;

    private String lsTransactID;

    private MaterialTextView mtv_status;
    private TextInputEditText tie_transaction_no, tie_fund_amt, tie_remarks;
    private MaterialAutoCompleteTextView auto_lodge_cal;
    private MaterialButton btn_submit, btn_cancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_turnover_funds, container, false);

        poLoading = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());
        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Funds.class);

        poLoading.InitDialog();
        poMessage.InitDialog();

        lsTransactID = (getArguments() == null || getArguments().getString("transact_id") == null) ? "" : getArguments().getString("transact_id");

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        mtv_status = view.findViewById(R.id.mtv_status);
        auto_lodge_cal = view.findViewById(R.id.auto_lodge_cal);
        tie_fund_amt = view.findViewById(R.id.tie_fund_amt);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_cancel = view.findViewById(R.id.btn_cancel);
    }

    private void InitDataReceiver(){

        //load transaction details
        mViewmodel.ObserveFundTurnovers(lsTransactID).observe(getViewLifecycleOwner(), new Observer<EFundTurnOver>() {
            @Override
            public void onChanged(EFundTurnOver eFundTurnOvers) {

                if (eFundTurnOvers == null){

                    //initiallize default data
                    loTurnover = new EFundTurnOver(
                            lsTransactID,
                            mViewmodel.GetCurrentDate(),
                            "",
                            "0.00",
                            "0.00",
                            "",
                            "1",
                            "",
                            "",
                            mViewmodel.GetUserInfo().getSUserIDxx(),
                            mViewmodel.GetCurrentDate(),
                            mViewmodel.GetCurrentDateTime()
                    );

                }else {

                    //initialize new data
                    loTurnover = eFundTurnOvers;
                }

                tie_transaction_no.setText(loTurnover.getSTransNox());
                tie_fund_amt.setText(loTurnover.getNAmountxx());
                tie_remarks.setText(loTurnover.getSRemarksx());

                if (lsTransactID.isEmpty()){

                    btn_submit.setText("Create Fund");

                    btn_submit.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.GONE);

                }else {

                    switch (loTurnover.getCTranStat()){

                        case "1":
                            mtv_status.setText("Pending for Approval");
                            mtv_status.setTextColor(Color.GRAY);
                            break;
                        case "2":
                            mtv_status.setText("Approved");
                            mtv_status.setTextColor(Color.GREEN);
                            break;
                        case "3":
                            mtv_status.setText("Disapproved");
                            mtv_status.setTextColor(Color.RED);
                            break;
                    }

                    //pending for approval, allowed for update of entry for regular members
                    if (loTurnover.getCTranStat().equalsIgnoreCase("1")){

                        //for regular user, allow update of fund entry, admiin or officers are allowed to approve or verify funds
                        if (mViewmodel.GetUserInfo().getNUserLevl() < 2){
                            btn_submit.setText("Update Fund");

                            btn_submit.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.GONE);
                        }else {

                            //do not allow modification of fields, only approval and disapproval
                            auto_lodge_cal.setEnabled(false);
                            tie_transaction_no.setEnabled(false);
                            tie_fund_amt.setEnabled(false);
                            tie_remarks.setEnabled(false);

                            btn_submit.setText("Approve Fund");
                            btn_cancel.setText("Disapprove Fund");

                            btn_submit.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                        }

                    }else {

                        //hide all buttons and disable the fields
                        btn_submit.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);

                        auto_lodge_cal.setEnabled(false);
                        tie_transaction_no.setEnabled(false);
                        tie_fund_amt.setEnabled(false);
                        tie_remarks.setEnabled(false);
                    }
                }

                //observe lodge calendars
                mViewmodel.GetLodgeCalendars().observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        loLodgeCalAdapter = new LodgeCalendarAdapter(
                                requireActivity(),
                                org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                                lodgeCalendarLists
                        );
                        auto_lodge_cal.setAdapter(loLodgeCalAdapter);

                        //load transactions year id
                        lodgeCalendarLists.stream()
                                .filter(loCalendar -> loCalendar.getSYearIDxx()
                                        .equalsIgnoreCase(loTurnover.getSYearIDxx()))
                                .findFirst()
                                .ifPresent(loCalendar ->{

                                    auto_lodge_cal.setText(loCalendar.getSLodgeNme() + "(" + loCalendar.getNYearxxxx() + ")", false);
                                    loSelectedCal = loCalendar;
                                });
                    }
                });
            }
        });
    }

    private boolean IsEntryOkay(){

        if (tie_fund_amt.getText() == null || tie_fund_amt.getText().toString().isEmpty()){
            tie_fund_amt.requestFocus();
            Toast.makeText(requireActivity(), "Please enter fund amount", Toast.LENGTH_SHORT).show();

            return false;
        }else if (loSelectedCal == null){
            tie_fund_amt.requestFocus();
            Toast.makeText(requireActivity(), "Please select from lodge calendars", Toast.LENGTH_SHORT).show();

            return false;
        }else if (Double.parseDouble(tie_fund_amt.getText().toString()) <= 0.00){
            tie_fund_amt.requestFocus();
            Toast.makeText(requireActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private void CreateFund(){

        mViewmodel.CreateFundTurnover(loTurnover, new VM_Funds.OnSubmit() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Submitting fund. Please wait...");
            }

            @Override
            public void OnSucces() {
                poLoading.DismissDialog();

                poMessage.ShowMessage(0, "Fund has been saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //retun to previous fragment
                        requireActivity().getSupportFragmentManager()
                                .popBackStack("turnover_funds", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoading.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });

    }

    private void UpdateFund(){

        mViewmodel.UpdateFundTurnover(loTurnover, new VM_Funds.OnSubmit() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Updating fund. Please wait...");
            }

            @Override
            public void OnSucces() {
                poLoading.DismissDialog();

                poMessage.ShowMessage(0, "Fund has been updated successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //retun to previous fragment
                        requireActivity().getSupportFragmentManager()
                                .popBackStack("fund_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoading.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });
    }

    private void ApproveFund(){

        mViewmodel.ApproveFundTurnover(loTurnover, new VM_Funds.OnSubmit() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Approving fund. Please wait...");
            }

            @Override
            public void OnSucces() {
                poLoading.DismissDialog();

                poMessage.ShowMessage(0, loTurnover.getCTranStat().equalsIgnoreCase("2") ? "Fund has been approved successfully" : "Fund has been disapproved successfully",
                        "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //retun to previous fragment
                        requireActivity().getSupportFragmentManager()
                                .popBackStack("fund_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoading.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) { poDialog.dismiss(); }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });
    }

    private void InitListener(){

        auto_lodge_cal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                loSelectedCal = (LodgeCalendarList) adapterView.getItemAtPosition(i);
                auto_lodge_cal.setText(loSelectedCal.getSLodgeNme() + "(" + loSelectedCal.getNYearxxxx() + ")", false);
            }
        });

        auto_lodge_cal.addTextChangedListener(new TextWatcher() {
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

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!IsEntryOkay()){
                    return;
                }

                loTurnover.setSYearIDxx(loSelectedCal.getSYearIDxx());
                loTurnover.setNAmountxx(tie_fund_amt.getText() == null ? "0.00" : tie_fund_amt.getText().toString());
                loTurnover.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());

                //initialize message based on text displayed
                String lsDialog = "Is your information complete?";
                switch (btn_submit.getText().toString()){
                    case "Update Fund":
                        UpdateFund();
                        break;
                    case "":
                        loTurnover.setCTranStat("2");
                        ApproveFund();
                        break;
                }

                poMessage.ShowMessage(2, !btn_submit.getText().toString().equalsIgnoreCase("Approve Fund") ? "Is your information complete?" : "Are you sure you want to approve this fund?",
                        "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        switch (btn_submit.getText().toString()){
                            case "Create Fund":
                                CreateFund();
                                break;
                            case "Update Fund":
                                UpdateFund();
                                break;
                            case "Approve Fund":
                                loTurnover.setCTranStat("2");
                                ApproveFund();
                                break;
                        }
                    }
                });
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!IsEntryOkay()){
                    return;
                }

                loTurnover.setSYearIDxx(loSelectedCal.getSYearIDxx());
                loTurnover.setNAmountxx(tie_fund_amt.getText() == null ? "0.00" : tie_fund_amt.getText().toString());
                loTurnover.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());

                poMessage.ShowMessage(2, "Are you sure you want to disapprove this fund?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        loTurnover.setCTranStat("3");
                        ApproveFund();
                    }
                });
            }
        });
    }
}