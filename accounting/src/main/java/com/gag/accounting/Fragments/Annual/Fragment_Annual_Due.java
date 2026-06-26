package com.gag.accounting.Fragments.Annual;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import com.gag.accounting.Dialog.Dialog_Annual_Entry;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.AnnualMemberAdapter;
import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.EAnnualMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Annual_Due extends Fragment {

    private VM_Annual mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private EAnnualMaster poAnualMaster;
    private LodgeCalendarAdapter poCalAdapter;
    private LodgeCalendarList loSelectCalendar;
    private AnnualMemberAdapter poDetailAdapter;
    private AnnualMembers loSelectedDetail;

    private List<AnnualMembers> laDetail;
    private String lsYearIDxx, lsLodgeIDxx, lsStackIDxx;
    private int lnSelectDetail = -1;


    private MaterialTextView btn_download, btn_view, btn_save_detail,  mtv_totaltrans,  mtv_totalcoll, mtv_status;
    private MaterialAutoCompleteTextView auto_lodge_cal, auto_lodge_member;
    private TextInputLayout til_lodge_cal;
    private TextInputEditText tie_transaction_no, tie_due, tie_remarks, tie_paid_amount, tie_due_amount, tie_remarks_detail;
    private CheckBox chk_exempt;
    private ImageButton btn_add_member;
    private ConstraintLayout layout_tools;
    private MaterialButton btn_save, btn_disapprove;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_annual_due, container, false);

        mViewModel = new ViewModelProvider(this).get(VM_Annual.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitListener();

        lsYearIDxx = (getArguments() == null || getArguments().getString("year_id") == null) ? "" : getArguments().getString("year_id");
        lsLodgeIDxx = (getArguments() == null || getArguments().getString("lodge_id") == null) ? "" : getArguments().getString("lodge_id");

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();

            switch (lsStackIDxx){

                case "annual_due_info":
                    til_lodge_cal.setEnabled(false);
                    layout_tools.setVisibility(View.VISIBLE);

                    DownloadInfo();
                    break;

                case "annual_due_entry":
                    til_lodge_cal.setEnabled(true);
                    layout_tools.setVisibility(View.GONE);

                    InitDataReceiver();
                    break;
            }
        }

        return view;
    }

    private void InitViews(View view){

        btn_download = view.findViewById(R.id.btn_download);
        btn_view = view.findViewById(R.id.btn_view);
        mtv_totaltrans = view.findViewById(R.id.mtv_totaltrans);
        mtv_totalcoll = view.findViewById(R.id.mtv_totalcoll);

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        til_lodge_cal = view.findViewById(R.id.til_lodge_cal);
        auto_lodge_cal = view.findViewById(R.id.auto_lodge_cal);
        mtv_status = view.findViewById(R.id.mtv_status);
        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        tie_due = view.findViewById(R.id.tie_due);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        tie_paid_amount = view.findViewById(R.id.tie_paid_amount);
        tie_due_amount = view.findViewById(R.id.tie_due_amount);
        tie_remarks_detail = view.findViewById(R.id.tie_remarks_detail);
        chk_exempt = view.findViewById(R.id.chk_exempt);

        layout_tools = view.findViewById(R.id.layout_tools);

        btn_save_detail = view.findViewById(R.id.btn_save_detail);
        btn_add_member = view.findViewById(R.id.btn_add_member);
        btn_save = view.findViewById(R.id.btn_save);
        btn_disapprove = view.findViewById(R.id.btn_disapprove);
    }

    private void DownloadInfo(){

        if (getArguments() == null) return;
        mViewModel.DownloadAnnual(lsLodgeIDxx, lsYearIDxx, "", "", new VM_Annual.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading annual billing. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();

                Toast.makeText(requireActivity(), "Annual billing downloaded successfully", Toast.LENGTH_SHORT).show();
                InitDataReceiver();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
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

    private void AllowFields(boolean fsAllowed){

        btn_add_member.setEnabled(fsAllowed);
        auto_lodge_cal.setEnabled(fsAllowed);
        tie_due.setEnabled(fsAllowed);
        tie_remarks.setEnabled(fsAllowed);
        tie_due_amount.setEnabled(fsAllowed);
        tie_paid_amount.setEnabled(fsAllowed);
        tie_remarks_detail.setEnabled(fsAllowed);
        chk_exempt.setEnabled(fsAllowed);

    }

    private void InitDataReceiver(){

        mViewModel.GetAnnualMaster(lsYearIDxx).observe(getViewLifecycleOwner(), new Observer<EAnnualMaster>() {
            @Override
            public void onChanged(EAnnualMaster eAnnualMaster) {

                ClearFields(new ArrayList<>(List.of(tie_transaction_no, auto_lodge_cal, tie_due, tie_remarks, mtv_totaltrans, mtv_totalcoll, auto_lodge_member, tie_due_amount, tie_paid_amount, tie_remarks_detail, chk_exempt)), true);

                if (eAnnualMaster == null){

                    poAnualMaster = new EAnnualMaster(
                            "",
                            "",
                            "1900-00-00",
                            "",
                            "0.00",
                            "0.00",
                            "1",
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate(),
                            mViewModel.GetCurrentDateTime()
                    );
                }else {
                    poAnualMaster = eAnnualMaster;
                }

                tie_transaction_no.setText(poAnualMaster.getSTransNox());
                tie_due.setText(poAnualMaster.getDDueDatex());
                tie_remarks.setText(poAnualMaster.getSRemarksx());

                //pending for approval, allowed for update of entry for accounts with user level
                if (poAnualMaster.getCTranStat().equalsIgnoreCase("1")){

                    mtv_status.setText("Pending for Approval");

                    //for regular user, allow update of fund entry, admin or officers are allowed to approve or verify funds
                    if ((mViewModel.GetUserInfo() == null ? 1 : mViewModel.GetUserInfo().getNUserLevl()) < 2){
                        btn_save.setText("Save Billing");

                        //allow modifications if pending but account level is USER level
                        AllowFields(true);

                        btn_save.setVisibility(View.VISIBLE);
                        btn_disapprove.setVisibility(View.GONE);
                    }else {
                        btn_save.setText("Approve");
                        btn_disapprove.setText("Disapprove");

                        btn_save.setVisibility(View.VISIBLE);
                        btn_disapprove.setVisibility(View.VISIBLE);

                        //allow modifications if pending but account level is NOT USER level
                        AllowFields(false);
                    }

                }else{

                    mtv_status.setText(poAnualMaster.getCTranStat().equalsIgnoreCase("2") ? "Approved" : "Disapproved");

                    //allow modifications if NOT PENDING
                    AllowFields(false);

                    btn_save.setVisibility(View.GONE);
                    btn_disapprove.setVisibility(View.GONE);
                }

                mViewModel.GetLodgeCalendars(lsLodgeIDxx).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        poCalAdapter = new LodgeCalendarAdapter(requireActivity(),
                                org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                                lodgeCalendarLists);

                        auto_lodge_cal.setAdapter(poCalAdapter);

                        if (poAnualMaster == null || poAnualMaster.getSYearIDxx().isEmpty()) return;
                        for (LodgeCalendarList loItem : lodgeCalendarLists){

                            if (loItem.getSYearIDxx().equalsIgnoreCase(poAnualMaster.getSYearIDxx())){

                                loSelectCalendar = loItem;
                                auto_lodge_cal.setText(loSelectCalendar.getSLodgeNme() + "(" + loSelectCalendar.getNYearxxxx() + ")", true);
                            }
                        }
                    }
                });

                //add to list the existing records of annual detail
                mViewModel.GetAnnualDetail(poAnualMaster.getSTransNox()).observe(getViewLifecycleOwner(), new Observer<List<AnnualMembers>>() {
                    @Override
                    public void onChanged(List<AnnualMembers> annualMembers) {

                        if (annualMembers == null) return;

                        mViewModel.ClearDetail();
                        for (AnnualMembers loItem : annualMembers){

                            if (!mViewModel.AddAnnualDetail(
                                    loItem.getSTransNox(),
                                    loItem.getSMemberID(),
                                    loItem.getSMemberNme(),
                                    loItem.getCExemptID(),
                                    loItem.getSRemarksx(),
                                    loItem.getNAmtDuexx(),
                                    loItem.getNAmtPaidx()
                            )){
                                Toast.makeText(requireActivity(), "Duplicate member detected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                mViewModel.GetAnnualDetail().observe(getViewLifecycleOwner(), new Observer<List<AnnualMembers>>() {
                    @Override
                    public void onChanged(List<AnnualMembers> annualMembers) {

                        if (annualMembers == null) return;
                        laDetail = annualMembers;

                        poDetailAdapter = new AnnualMemberAdapter(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, annualMembers);
                        auto_lodge_member.setAdapter(poDetailAdapter);

                        //compute total amount paid and due from detail
                        double ldbl_paid = 0.00, ldbl_due = 0.00;
                        for (AnnualMembers loAnnual : annualMembers){

                            ldbl_paid += Double.parseDouble(loAnnual.getNAmtPaidx());
                            ldbl_due += Double.parseDouble(loAnnual.getNAmtDuexx());
                        }

                        mtv_totaltrans.setText(String.valueOf(ldbl_due));
                        mtv_totalcoll.setText(String.valueOf(ldbl_paid));
                    }
                });

            }
        });
    }

    private void SaveAnnual(){

        //check transaction's primary required fields if valid
        if (poAnualMaster == null){
            Toast.makeText(requireActivity(), "Transaction is not initialized", Toast.LENGTH_SHORT).show();
            return;
        }else if (loSelectCalendar == null || loSelectCalendar.getSYearIDxx().isEmpty()) {
            Toast.makeText(requireActivity(), "Lodge Year is not initialized", Toast.LENGTH_SHORT).show();
            auto_lodge_cal.requestFocus();
            return;
        } else if (tie_due.getText() == null || tie_due.getText().toString().isEmpty() || tie_due.getText().toString().equalsIgnoreCase("1900-00-00")) {
            Toast.makeText(requireActivity(), "Due Date is not initialized", Toast.LENGTH_SHORT).show();
            tie_due.requestFocus();
            return;
        } else if (laDetail == null || laDetail.size() < 1){
            Toast.makeText(requireActivity(), "Transaction Detail is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //initialize master properties
        poAnualMaster.setSYearIDxx(loSelectCalendar.getSYearIDxx());
        poAnualMaster.setDDueDatex(tie_due.getText().toString());
        poAnualMaster.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());

        poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
            @Override
            public void OnPositive(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();
            }

            @Override
            public void OnNegative(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();

                mViewModel.SaveAnnualDue(poAnualMaster, laDetail, new VM_Annual.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Creating annual dues. Please wait . . .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(0, "Annual due saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
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
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
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

    private void ApproveAnnual(String fsStatus){

        poMessage.ShowMessage(2, "Are you sure you want to " + (fsStatus.equalsIgnoreCase("2") ? "approve" : "disapprove") + " this transaction?", "No", "Yes", new Message_Dialog.OnDialogClick() {
            @Override
            public void OnPositive(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();
            }

            @Override
            public void OnNegative(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();

                if (poAnualMaster == null){
                    Toast.makeText(requireActivity(), "Transaction is not initialized", Toast.LENGTH_SHORT).show();
                    return;
                }else if(poAnualMaster.getSTransNox().isEmpty()){
                    Toast.makeText(requireActivity(), "Transaction ID not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set status change
                poAnualMaster.setCTranStat(fsStatus);

                //save status change
                mViewModel.ApproveAnnualDue(poAnualMaster, new VM_Annual.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Saving approval. Please wait . . .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(0, "Approval has been saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                requireActivity().getSupportFragmentManager()
                                        .popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();

                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void InitListener(){

        auto_lodge_cal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (poCalAdapter == null) return;
                loSelectCalendar = poCalAdapter.getItem(i);

                auto_lodge_cal.setText(loSelectCalendar.getSLodgeNme() + "(" + loSelectCalendar.getNYearxxxx() + ")", false);
            }
        });

        tie_due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectCalendar == null){
                    Toast.makeText(requireActivity(), "Please select lodge calendar first", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar loCalendar = Calendar.getInstance();
                DatePickerDialog loValidPicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        tie_due.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, Integer.parseInt(loSelectCalendar.getNYearxxxx()),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                );

                // Restrict to chosen year
                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(loSelectCalendar.getNYearxxxx()), Calendar.JANUARY, 1);

                Calendar maxDate = Calendar.getInstance();
                maxDate.set(Integer.parseInt(loSelectCalendar.getNYearxxxx()), Calendar.DECEMBER, 31);

                loValidPicker.getDatePicker().setMinDate(minDate.getTimeInMillis());
                loValidPicker.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                loValidPicker.show();

            }
        });

        auto_lodge_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (poDetailAdapter == null) return;
                loSelectedDetail = poDetailAdapter.getItem(i);
                lnSelectDetail = i;

                //display details
                auto_lodge_member.setText(loSelectedDetail.getSMemberNme(), false);
                tie_paid_amount.setText(loSelectedDetail.getNAmtPaidx());
                tie_due_amount.setText(loSelectedDetail.getNAmtDuexx());
                tie_remarks_detail.setText(loSelectedDetail.getSRemarksx());
                chk_exempt.setChecked(loSelectedDetail.getCExemptID().equalsIgnoreCase("1"));
            }
        });

        tie_due_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //if field is empty or selected detail is empty, hide save detail button
                if (charSequence.length() < 1 || loSelectedDetail == null){
                    btn_save_detail.setVisibility(View.GONE);
                } else if (Double.parseDouble(loSelectedDetail.getNAmtDuexx()) == Double.parseDouble(charSequence.toString())) {
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        tie_paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //if field is empty or selected detail is empty, hide save detail button
                if (charSequence.length() < 1 || loSelectedDetail == null){
                    btn_save_detail.setVisibility(View.GONE);
                } else if (Double.parseDouble(loSelectedDetail.getNAmtPaidx()) == Double.parseDouble(charSequence.toString())) {
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        tie_remarks_detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //if field is empty or selected detail is empty, hide save detail button
                if (charSequence.length() < 1 || loSelectedDetail == null){
                    btn_save_detail.setVisibility(View.GONE);
                } else if (loSelectedDetail.getSRemarksx().equalsIgnoreCase(charSequence.toString())) {
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        chk_exempt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton compoundButton, boolean b) {

                if (loSelectedDetail == null){
                    btn_save_detail.setVisibility(View.GONE);
                }else if (loSelectedDetail.getCExemptID().equalsIgnoreCase(chk_exempt.isChecked() ? "1" : "0")){
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadInfo();
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Annual_Summary loSummary = new Fragment_Annual_Summary();

                Bundle loBundle = new Bundle();
                loBundle.putString("year_id", lsYearIDxx);
                loBundle.putString("lodge_id", lsLodgeIDxx);

                loSummary.setArguments(loBundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("annual_summary", loSummary))
                        .addToBackStack("annual_summary")
                        .commit();
            }
        });

        btn_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lsLodgeIDxx.isEmpty()){
                    Toast.makeText(requireActivity(), "Lodge ID is not initialized", Toast.LENGTH_SHORT).show();
                    return;
                }

                Dialog_Annual_Entry loEntry = new Dialog_Annual_Entry(requireActivity(), lsLodgeIDxx, mViewModel, getViewLifecycleOwner(), new Dialog_Annual_Entry.OnSubmitEntry() {
                    @Override
                    public void OnSubmit(AnnualMembers loEntry) {

                        if (!mViewModel.AddAnnualDetail(
                                "",
                                loEntry.getSMemberID(),
                                loEntry.getSMemberNme(),
                                loEntry.getCExemptID(),
                                loEntry.getSRemarksx(),
                                loEntry.getNAmtDuexx(),
                                loEntry.getNAmtPaidx()
                        )){
                            poMessage.ShowMessage(1, "Member is already added", "Okay", "", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {}
                            });
                        }
                    }
                });
                loEntry.Show();
            }
        });

        btn_save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lnSelectDetail < 0) return;

                mViewModel.ReplaceAnnualDetail(lnSelectDetail, tie_due_amount.getText().toString(), tie_paid_amount.getText().toString(), tie_remarks_detail.getText().toString(), chk_exempt.isChecked() ? "1" : "0");
                btn_save_detail.setVisibility(View.GONE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_save.getText().toString().equalsIgnoreCase("Save Billing")){

                    SaveAnnual();
                }else {

                    ApproveAnnual("2");
                }
            }
        });

        btn_disapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApproveAnnual("3");
            }
        });
    }
}