package com.gag.accounting.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.Room.Entities.EMemberInfo;

import java.util.ArrayList;
import java.util.List;

public class Dialog_Annual_Entry {

    private Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final VM_Annual loViewModel;

    private MemberAdapter loMemberAdapter;
    private EMemberInfo loSelectMember;

    private final LifecycleOwner loOwner;
    private final OnSubmitEntry loCallback;

    private String lsLodgeIDxx;
    private int lnType = -1;

    private MaterialAutoCompleteTextView auto_lodge_member, autoType;
    private TextInputEditText tieTransactionAmount, tieRemarks;
    private CheckBox chk_exempt;
    private MaterialButton btnSubmit;

    public interface OnSubmitEntry{
        void OnSubmit(AnnualMembers loEntry);
    }

    public Dialog_Annual_Entry(Context context, String fsLodgeIDxx, VM_Annual foViewModel, LifecycleOwner foOwner, OnSubmitEntry foCallback) {

        loInstance = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_annual_entry, null);
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
        autoType = view.findViewById(R.id.auto_type);
        tieTransactionAmount = view.findViewById(R.id.tie_transaction_amount);
        tieRemarks = view.findViewById(R.id.tie_remarks);
        chk_exempt = view.findViewById(R.id.chk_exempt);
        btnSubmit = view.findViewById(R.id.btn_submit);

    }

    private void InitDataReceiver(){

        loViewModel.GetMemberList().observe(loOwner, new Observer<List<EMemberInfo>>() {
            @Override
            public void onChanged(List<EMemberInfo> eMemberInfos) {

                if (eMemberInfos == null) return;

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

        autoType.setAdapter(new ArrayAdapter<>(
                loInstance,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                loViewModel.GetAmountTypes()
        ));
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

        autoType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnType = i;
                autoType.setText(loViewModel.GetAmountTypes().get(i), false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectMember == null){
                    Toast.makeText(loInstance, "Please select member", Toast.LENGTH_SHORT).show();
                    return;
                }else if (lnType < 0){
                    Toast.makeText(loInstance, "Please select transaction type", Toast.LENGTH_SHORT).show();
                    return;
                }else if (tieTransactionAmount.getText() == null || tieTransactionAmount.getText().toString().isEmpty()){
                    Toast.makeText(loInstance, "Please enter transaction amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                AnnualMembers loMembers = new AnnualMembers(
                        "",
                        loSelectMember.getSMemberID(),
                        loSelectMember.getSFrstName() + " " + loSelectMember.getSLastName(),
                        chk_exempt.isChecked() ? "1" : "0",
                        tieRemarks.getText().toString(),
                        lnType == 0 ? tieTransactionAmount.getText().toString() : "0.00",
                        lnType == 1 ? tieTransactionAmount.getText().toString() : "0.00"
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
