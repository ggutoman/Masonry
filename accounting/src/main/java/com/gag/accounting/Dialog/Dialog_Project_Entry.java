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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.gag.accounting.ViewModel.VM_Projects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectDetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Dialog_Project_Entry {

    private Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final VM_Projects loViewModel;

    private MemberAdapter loMemberAdapter;
    private EMemberInfo loSelectMember;

    private final LifecycleOwner loOwner;
    private final OnSubmitEntry loCallback;

    private String lsLodgeIDxx;

    private MaterialAutoCompleteTextView auto_lodge_member;
    private TextInputEditText tie_pledge_date, tie_pledge_amount, tie_or_no, tie_paid_amount;
    private MaterialButton btn_submit;

    public interface OnSubmitEntry{
        void OnSubmit(ProjectDetail loEntry);
    }

    public Dialog_Project_Entry(Context context, String fsLodgeIDxx, VM_Projects foViewModel, LifecycleOwner foOwner, OnSubmitEntry foCallback) {

        loInstance = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_project_entry, null);
        loCallback = foCallback;
        lsLodgeIDxx = fsLodgeIDxx;

        // Build and show dialog
        poDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        poDialog.setCancelable(true);
        if (poDialog.getWindow() != null) {
            poDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        loViewModel = foViewModel;
        loOwner = foOwner;

        InitViews();
        InitDataReceiver();
        InitListeners();
    }

    private void InitViews(){

        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        tie_pledge_date = view.findViewById(R.id.tie_pledge_date);
        tie_pledge_amount = view.findViewById(R.id.tie_pledge_amount);
        tie_or_no = view.findViewById(R.id.tie_or_no);
        tie_paid_amount = view.findViewById(R.id.tie_paid_amount);
        btn_submit = view.findViewById(R.id.btn_submit);

    }

    private void InitDataReceiver(){

        loViewModel.GetMemberList().observe(loOwner, new Observer<List<EMemberInfo>>() {
            @Override
            public void onChanged(List<EMemberInfo> eMemberInfos) {

                if (eMemberInfos == null) return;

                //only show members of selected lodge
                List<EMemberInfo> lodgeMembers = new ArrayList<>();
                for (EMemberInfo loMember : eMemberInfos){

                    if (loMember.getSLodgeIDx().equalsIgnoreCase(lsLodgeIDxx)){
                        lodgeMembers.add(loMember);
                    }
                }

                loMemberAdapter = new MemberAdapter(loInstance,
                        androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                        lodgeMembers);

                auto_lodge_member.setAdapter(loMemberAdapter);
            }
        });
    }

    private void InitListeners(){

        auto_lodge_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (loMemberAdapter == null) return;

                loSelectMember = loMemberAdapter.getItem(i);
                auto_lodge_member.setText(loSelectMember.getSFrstName() + " " + loSelectMember.getSLastName(), false);
            }
        });

        tie_pledge_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();
                DatePickerDialog loValidPicker = new DatePickerDialog(loInstance, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        tie_pledge_date.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                );

                //Restrict minimum selection after the current day
                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(loViewModel.GetFormattedDate(loViewModel.GetCurrentDate(), "yyyy")),
                                Integer.parseInt(loViewModel.GetFormattedDate(loViewModel.GetCurrentDate(), "MM")),
                                Integer.parseInt(loViewModel.GetFormattedDate(loViewModel.GetCurrentDate(), "dd") + 1
                            )
                );

                loValidPicker.show();

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectMember == null){
                    Toast.makeText(loInstance, "Please select member", Toast.LENGTH_SHORT).show();
                    return;
                }else if (tie_pledge_date.getText() == null || tie_pledge_date.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please select promised date", Toast.LENGTH_SHORT).show();
                    return;
                }else if (tie_pledge_amount.getText() == null || tie_pledge_amount.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter promised amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                ProjectDetail loMembers = new ProjectDetail(
                        "",
                        loSelectMember.getSMemberID(),
                        loSelectMember.getSFrstName() + " " + loSelectMember.getSLastName(),
                        tie_or_no.getText() == null ? "" : tie_or_no.getText().toString(),
                        tie_pledge_date.getText().toString(),
                        tie_pledge_amount.getText() == null ? "0.00" : tie_pledge_amount.getText().toString(),
                        tie_paid_amount.getText() == null ? "0.00" : tie_paid_amount.getText().toString()
                );
                loCallback.OnSubmit(loMembers);

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
