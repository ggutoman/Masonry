package com.gag.accounting.Fragments.Receipt;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Transactions;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Core.Annual;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Receipt_Entry extends Fragment {

    private VM_Receipts mViewModel;
    private String lsTransNox, lsMemberID;
    private EReceiptMaster loReceipt;
    private List<EReceiptDetail> laTransactions;

    private String lsSelectType;
    private EReceiptDetail loSelectDetail;

    // TextViews
    private MaterialTextView mtv_title;
    private MaterialTextView mtv_status;
    private MaterialTextView btn_download;
    private MaterialTextView btn_view;
    private MaterialTextView mtv_totaltrans_label;
    private MaterialTextView mtv_totalpledge;
    private MaterialTextView btn_save_detail;

    // TextInputEditTexts
    private TextInputEditText tie_transaction_no;
    private TextInputEditText tie_dtransact;
    private TextInputEditText tie_or;
    private TextInputEditText tie_remarks;
    private TextInputEditText tie_trans_amount;
    private TextInputEditText tie_paid_amount;

    // AutoCompleteTextViews
    private MaterialAutoCompleteTextView auto_lodge_member;
    private MaterialAutoCompleteTextView auto_pay_type;
    private MaterialAutoCompleteTextView auto_source_type;
    private MaterialAutoCompleteTextView auto_source_transaction;

    // Buttons
    private ImageButton btn_add_detail;
    private MaterialButton btn_save;

    // Layouts
    private ConstraintLayout layout_tools;
    private LinearLayout layout_buttons;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__receipt, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Receipts.class);
        laTransactions = new ArrayList<>();

        InitViews(view);

        return view;
    }

    private void InitViews(View view){

        // TextViews
        mtv_title        = view.findViewById(R.id.mtv_title);
        mtv_status       = view.findViewById(R.id.mtv_status);
        btn_download     = view.findViewById(R.id.btn_download);
        btn_view         = view.findViewById(R.id.btn_view);
        mtv_totaltrans_label = view.findViewById(R.id.mtv_totaltrans_label);
        mtv_totalpledge  = view.findViewById(R.id.mtv_totalpledge);
        btn_save_detail  = view.findViewById(R.id.btn_save_detail);

// TextInputEditTexts
        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        tie_dtransact      = view.findViewById(R.id.tie_dtransact);
        tie_or             = view.findViewById(R.id.tie_or);
        tie_remarks        = view.findViewById(R.id.tie_remarks);
        tie_trans_amount   = view.findViewById(R.id.tie_trans_amount);
        tie_paid_amount    = view.findViewById(R.id.tie_paid_amount);

// AutoCompleteTextViews
        auto_lodge_member       = view.findViewById(R.id.auto_lodge_member);
        auto_pay_type           = view.findViewById(R.id.auto_pay_type);
        auto_source_type        = view.findViewById(R.id.auto_source_type);
        auto_source_transaction = view.findViewById(R.id.auto_source_transaction);

// Buttons
        btn_add_detail = view.findViewById(R.id.btn_add_detail);
        btn_save       = view.findViewById(R.id.btn_save);

// Layouts
        layout_tools   = view.findViewById(R.id.layout_tools);
        layout_buttons = view.findViewById(R.id.layout_buttons);

    }

    private void InitDataReceiver(){

        if (getArguments() == null || getArguments().getString("member_id") == null){
            Toast.makeText(requireActivity(), "Member ID is not initialized", Toast.LENGTH_SHORT).show();

            requireActivity().getSupportFragmentManager().popBackStack("receipt_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }
        lsMemberID = getArguments() == null || getArguments().getString("member_id") == null ? "" : getArguments().getString("member_id");
        lsTransNox = getArguments() == null || getArguments().getString("transaction_id") == null ? "" : getArguments().getString("transaction_id");

        auto_pay_type.setAdapter(new ArrayAdapter<>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mViewModel.GetPaymentTypes()
        ));

        auto_source_type.setAdapter(new ArrayAdapter<>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mViewModel.GetSources()
        ));

        mViewModel.ObserveReceiptMaster(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EReceiptMaster>() {
            @Override
            public void onChanged(EReceiptMaster eReceiptMaster) {

                //clear fields
                ClearFields(List.of(auto_lodge_member, auto_pay_type, tie_or, tie_remarks, auto_source_type, auto_source_transaction, tie_trans_amount, tie_paid_amount), true);

                if (eReceiptMaster == null){

                    //initialize new record
                    loReceipt = new EReceiptMaster(
                            "",
                            mViewModel.GetCurrentDate(),
                            lsMemberID,
                            "0",
                            "0.00",
                            "",
                            "",
                            "0",
                            "0",
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate(),
                            mViewModel.GetCurrentDateTime()
                    );
                }else {

                    loReceipt = eReceiptMaster;
                }

                //display master values
                tie_transaction_no.setText(loReceipt.getSTransNox());
                tie_dtransact.setText(loReceipt.getDTransact());

                switch (loReceipt.getCTranStat()){

                    case "0":
                        mtv_status.setText("Open");
                        break;
                    case "1":
                        mtv_status.setText("Approve");
                        break;
                    case "2":
                        mtv_status.setText("Disapprove");
                        break;
                }

                AllowFields(List.of(auto_pay_type, tie_or, tie_remarks, btn_add_detail, tie_paid_amount) ,loReceipt.getCTranStat().equalsIgnoreCase("0"));

                auto_lodge_member.setText(mViewModel.GetMemberInfo(lsMemberID).getSFrstName() + " " + mViewModel.GetMemberInfo(lsMemberID).getSLastName());
                tie_or.setText(loReceipt.getSORNoxxxx());
                tie_remarks.setText(loReceipt.getSRemarksx());

                //get selected transaction
                mViewModel.ObserveSelectTransaction().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {

                        //load list of transactions from type
                        mViewModel.ObserveReceiptDetail(loReceipt.getSTransNox()).observe(getViewLifecycleOwner(), new Observer<List<EReceiptDetail>>() {
                            @Override
                            public void onChanged(List<EReceiptDetail> eReceiptDetails) {

                                if (eReceiptDetails == null) return;

                                for (EReceiptDetail loDetail : eReceiptDetails){

                                    //load list of transactions based on selected type
                                    if (loDetail.getSSourceCD().equalsIgnoreCase(s)){
                                        laTransactions.add(loDetail);
                                    }
                                }

                                Adapter_Receipt_Transactions loAdapter = new Adapter_Receipt_Transactions( requireActivity(), laTransactions)
                                //auto_source_transaction.setAdapter(loAdapter);
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

    private void AllowFields(List<View> fields, boolean isAllowed) {

        for (View view : fields) {

            view.setEnabled(isAllowed);
        }
    }

    private void InitListener(){

        auto_source_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_type.getAdapter() == null) return;

                lsSelectType = mViewModel.GetSources().get(i).getFsID();
                mViewModel.SelectTransaction(lsSelectType);
            }
        });

        auto_source_transaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_transaction.getAdapter() == null) return;

                mViewModel.SelectTransaction(lsSelectType);
            }
        });
    }
}