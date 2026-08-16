package com.gag.accounting.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Sources;
import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Transactions;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ReceiptDetail;

import java.util.ArrayList;
import java.util.List;

public class Dialog_Receipt_Entry {

    private final Context loInstance;
    private final View view;
    private final AlertDialog poDialog;
    private final VM_Receipts loViewModel;

    private final LifecycleOwner loOwner;
    private final OnSubmitEntry loCallback;

    private String lsMemberID;
    private String lsSelectType;

    private ReceiptDetail loSelectSource;

    private MaterialAutoCompleteTextView auto_source_type, auto_source_transaction;
    private MaterialTextView btn_download_source;
    private MaterialButton btn_add;
    private TextInputLayout til_source_transaction;
    private TextInputEditText tie_paid_amount;

    public interface OnSubmitEntry{
        void OnSubmit(ReceiptDetail loEntry);
    }

    public Dialog_Receipt_Entry(Context context, String fsMemberIDxx, VM_Receipts foViewModel, LifecycleOwner foOwner, OnSubmitEntry foCallback) {

        loInstance = context;
        view = LayoutInflater.from(context).inflate(R.layout.fragment__receipt_entry, null);
        loCallback = foCallback;
        lsMemberID = fsMemberIDxx;

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

        auto_source_type = view.findViewById(R.id.auto_source_type);
        auto_source_transaction = view.findViewById(R.id.auto_source_transaction);
        btn_download_source = view.findViewById(R.id.btn_download_source);
        btn_add = view.findViewById(R.id.btn_add);
        til_source_transaction = view.findViewById(R.id.til_source_transaction);
        tie_paid_amount = view.findViewById(R.id.tie_paid_amount);
    }

    private void InitDataReceiver(){

        auto_source_type.setAdapter(
                new Adapter_Receipt_Sources(
                        loInstance,
                        loViewModel.GetSources()
                )
        );

        //get selected type and transactions
        loViewModel.ObserveSelectType().observe(loOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s == null || s.isEmpty()) return;

                switch (s){

                    case "DUES":

                        //get all approved annual dues only
                        loViewModel.GetAnnualReceipts(lsMemberID).observe(loOwner, new Observer<List<AnnualMembers>>() {
                            @Override
                            public void onChanged(List<AnnualMembers> annualMembers) {

                                if (annualMembers == null) return;

                                List<ReceiptDetail> laReceipts = new ArrayList<>();
                                for (AnnualMembers loMember : annualMembers){

                                    double ldbl_bal = Double.parseDouble(loMember.getNAmtDuexx()) - Double.parseDouble(loMember.getNAmtPaidx());
                                    if (ldbl_bal > 0){

                                        laReceipts.add(
                                                new ReceiptDetail(
                                                        "",
                                                        s,
                                                        loMember.getSTransNox(),
                                                        "Member's Contribution",
                                                        String.valueOf(ldbl_bal)
                                                )
                                        );

                                    }
                                }

                                auto_source_transaction.setAdapter(
                                        new Adapter_Receipt_Transactions(
                                                loInstance,
                                                R.layout.list_receipt_details,
                                                laReceipts
                                        )
                                );
                            }
                        });
                        break;
                    case "PROJ":

                        //get all approved projects only
                        loViewModel.GetProjects(lsMemberID).observe(loOwner, new Observer<List<MemberPledges>>() {
                            @Override
                            public void onChanged(List<MemberPledges> projmembers) {

                                if (projmembers == null) return;

                                List<ReceiptDetail> laReceipts = new ArrayList<>();
                                for (MemberPledges loMember : projmembers){

                                    double ldbl_bal = Double.parseDouble(loMember.getNPledgexx()) - Double.parseDouble(loMember.getNAmtPaidx());
                                    if (ldbl_bal > 0){

                                        laReceipts.add(
                                                new ReceiptDetail(
                                                        "",
                                                        s,
                                                        loMember.getSProjctCd(),
                                                        loMember.getSProjctNm(),
                                                        String.valueOf(ldbl_bal)
                                                )
                                        );

                                    }
                                }

                                auto_source_transaction.setAdapter(
                                        new Adapter_Receipt_Transactions(
                                                loInstance,
                                                R.layout.list_receipt_details,
                                                laReceipts
                                        )
                                );
                            }
                        });
                        break;
                     case "BINT":
                        break;
                    case "OTH":
                        break;
                }
            }
        });
    }

    private void DownloadAnnuals(){

        loViewModel.DownloadSourceAnnuals(lsMemberID, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                Toast.makeText(loInstance, "Downloading annual transactions. Please wait . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFinished(String fsMessage) {
                Toast.makeText(loInstance, fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DownloadProjects(){

        loViewModel.DownloadMemberPledges(lsMemberID, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                Toast.makeText(loInstance, "Downloading project transactions. Please wait . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFinished(String fsMessage) {
                Toast.makeText(loInstance, fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DownloadSources(String fsSource){

        switch (fsSource){

            case "DUES":
                DownloadAnnuals();
                break;
            case "PROJ":
                DownloadProjects();
                break;
        }
    }

    private void InitListeners(){

        auto_source_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_type.getAdapter() == null) return;
                lsSelectType = loViewModel.GetSources().get(i).getFsID();

                auto_source_type.setText(loViewModel.GetSources().get(i).getFsDescript());
                loViewModel.SelectType(lsSelectType);

                //clear adapter for new list
                tie_paid_amount.setText("0.00");
                auto_source_transaction.setText("");
                auto_source_transaction.setAdapter(null);

                //enable paidamount based on selected type
                tie_paid_amount.setEnabled(!(lsSelectType.equalsIgnoreCase("DUES") || lsSelectType.equalsIgnoreCase("PROJ")));

                //show other fields  based on selected type
                til_source_transaction.setVisibility(lsSelectType.equalsIgnoreCase("DUES") || lsSelectType.equalsIgnoreCase("PROJ") ? View.VISIBLE : View.GONE);
                btn_download_source.setVisibility(lsSelectType.equalsIgnoreCase("DUES") || lsSelectType.equalsIgnoreCase("PROJ") ? View.VISIBLE : View.GONE);
            }
        });

        auto_source_transaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_transaction.getAdapter() == null) return;
                loSelectSource = ((ReceiptDetail) auto_source_transaction.getAdapter().getItem(i));

                auto_source_transaction.setText(loSelectSource.getSSourceDscrpt().isEmpty() ? loSelectSource.getSSourceNo() : loSelectSource.getSSourceDscrpt(), false);
                tie_paid_amount.setText(loSelectSource.getNAmtPaidx());
            }
        });

        btn_download_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lsSelectType == null || lsSelectType.isEmpty()){
                    Toast.makeText(loInstance, "Please select transaction type", Toast.LENGTH_SHORT).show();
                    return;
                }
                DownloadSources(lsSelectType);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lsSelectType.equalsIgnoreCase("DUES") || lsSelectType.equalsIgnoreCase("PROJ")){

                    if (loSelectSource == null){
                        Toast.makeText(loInstance, "Please select transaction", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }else{

                    loSelectSource = new ReceiptDetail(
                            "",
                            lsSelectType,
                            "",
                            "",
                            tie_paid_amount.getText().toString()
                    );
                }
                poDialog.dismiss();
                loCallback.OnSubmit(loSelectSource);
            }
        });
    }

    public void Show(){

        if (!poDialog.isShowing()){
            poDialog.show();
        }
    }

}
