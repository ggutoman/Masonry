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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    private RadioGroup radiogroup;
    private MaterialTextView mtv_name;
    private MaterialButton btn_submit;

    public interface OnSubmitEntry{
        void OnSubmit(String fStatus);
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
        InitListeners();

        mtv_name.setText(lsProjectNme);
    }

    private void InitViews(){
        radiogroup = view.findViewById(R.id.radiogroup);
        mtv_name = view.findViewById(R.id.mtv_name);
        btn_submit = view.findViewById(R.id.btn_submit);
    }

    private void InitListeners(){

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialze selected status
                if (radiogroup.getCheckedRadioButtonId() == R.id.radio_planned) {
                    lnSelectStatus = 0;
                }else if (radiogroup.getCheckedRadioButtonId() == R.id.radio_ongoing) {
                    lnSelectStatus = 1;
                }else if (radiogroup.getCheckedRadioButtonId() == R.id.radio_completed) {
                    lnSelectStatus = 2;
                }else if (radiogroup.getCheckedRadioButtonId() == R.id.radio_cancelled) {
                    lnSelectStatus = 3;
                }

                //check selected status
                if (lnSelectStatus < 0){
                    Toast.makeText(loInstance, "Please select status", Toast.LENGTH_SHORT).show();
                    return;
                }

                //submit status
                loCallback.OnSubmit(String.valueOf(lnSelectStatus));

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
