package com.gag.accounting.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.gag.appdriver.App.Models.AnnualMembers;

public class Dialog_Annual_Entry {

    private Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final VM_Annual loViewModel;
    private final LifecycleOwner loOwner;
    private final OnSubmitEntry loCallback;

    private int lnType = -1;

    private MaterialAutoCompleteTextView autoLodgeCal, autoType;
    private TextInputEditText tieTransactionAmount, tieRemarks;
    private CheckBox chk_exempt;
    private MaterialButton btnSubmit, btnCancel;

    public interface OnSubmitEntry{
        void OnSubmit(AnnualMembers loEntry);
    }

    public Dialog_Annual_Entry(Context context, VM_Annual foViewModel, LifecycleOwner foOwner, OnSubmitEntry foCallback) {

        loInstance = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_annual_entry, null);
        loCallback = foCallback;

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(false)
                .create();

        poDialog.setCancelable(false);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        loViewModel = foViewModel;
        loOwner = foOwner;

        InitViews();
        InitListeners();
    }

    private void InitViews(){

        autoLodgeCal = view.findViewById(R.id.auto_lodge_cal);
        autoType = view.findViewById(R.id.auto_type);
        tieTransactionAmount = view.findViewById(R.id.tie_transaction_amount);
        tieRemarks = view.findViewById(R.id.tie_remarks);
        chk_exempt = view.findViewById(R.id.chk_exempt);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);

    }

    private void InitListeners(){

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lnType < 0){
                    Toast.makeText(loInstance, "Please select transaction type", Toast.LENGTH_SHORT).show();
                    return;
                }else if (tieTransactionAmount.getText() == null || tieTransactionAmount.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter transaction amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                AnnualMembers loMembers = new AnnualMembers(
                        "",
                        "",
                        chk_exempt.isChecked() ? "1" : "0",
                        tieRemarks.getText().toString(),
                        lnType == 0 ? tieTransactionAmount.getText().toString() : "0.00",
                        lnType == 1 ? tieTransactionAmount.getText().toString() : "0.00"
                );
                loCallback.OnSubmit(loMembers);
            }
        });
    }

}
