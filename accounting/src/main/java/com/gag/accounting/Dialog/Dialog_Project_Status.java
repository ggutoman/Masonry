package com.gag.accounting.Dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Projects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Dialog_Project_Status {

    private Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final VM_Projects loViewModel;

    private final String lsProjectNme;
    private int lnSelectStatus = -1;
    private final OnSubmitEntry loCallback;

    private MaterialAutoCompleteTextView auto_status;
    private MaterialTextView mtv_name;
    private TextInputEditText tie_disb_amount;
    private MaterialButton btn_submit;

    public interface OnSubmitEntry{
        void OnSubmit(String fStatus, String fDisbAmount);
    }

    public Dialog_Project_Status(Context context, String fsProjectNme, VM_Projects foViewModel, OnSubmitEntry foCallback) {

        loInstance = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_project_status, null);
        loCallback = foCallback;

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        poDialog.setCancelable(true);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        lsProjectNme = fsProjectNme;
        loViewModel = foViewModel;

        InitViews();
        InitDataReceiver();
        InitListeners();
    }

    private void InitViews(){

        auto_status = view.findViewById(R.id.auto_status);
        mtv_name = view.findViewById(R.id.mtv_name);
        tie_disb_amount = view.findViewById(R.id.tie_disb_amount);
        btn_submit = view.findViewById(R.id.btn_submit);

    }

    private void InitDataReceiver(){

        mtv_name.setText(lsProjectNme);

        auto_status.setAdapter(
                new ArrayAdapter<>(
                        loInstance,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        loViewModel.GetProjectStatus()
                )
        );
    }

    private void InitListeners(){

        auto_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectStatus = i;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lnSelectStatus < 0){
                    Toast.makeText(loInstance, "Please select status", Toast.LENGTH_SHORT).show();
                    return;
                }

                //initialize default value for disbursement
                double ldbl_disb = Double.parseDouble(tie_disb_amount.getText() == null || tie_disb_amount.getText().toString().isEmpty() ? "0.00" : tie_disb_amount.getText().toString());

                //allow disbursement amount based on status
                boolean isDisbAllowed = true;
                switch (lnSelectStatus){

                    case 1:
                    case 3:
                        if (ldbl_disb <= 0){
                            isDisbAllowed = false;
                            Toast.makeText(loInstance, "Disbursement amount is required for " + (lnSelectStatus == 1 ? "On Going status" : "Completed status"), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        if (ldbl_disb >= 0){
                            isDisbAllowed = false;
                            Toast.makeText(loInstance, "Disbursement amount is not allowed for the selected status", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

                if (!isDisbAllowed){
                    return;
                }
                loCallback.OnSubmit(String.valueOf(lnSelectStatus), tie_disb_amount.getText().toString());

                if (poDialog.isShowing()){
                    poDialog.dismiss();
                }
            }
        });
    }

    public void Show(){
        if (!poDialog.isShowing()){
            poDialog.show();
        }
    }

}
