package com.gag.accounting.Fragments.Receipt;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Sources;
import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Transactions;
import com.gag.accounting.Dialog.Dialog_Receipt_Entry;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Receipt extends Fragment {

    private VM_Receipts mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoading;

    private String lsTransNox, lsMemberID;
    private EReceiptMaster loReceipt;
    private List<ReceiptDetail> laDetails;

    private int lnSelectPaym = -1;
    private String lsStackIDxx, lsSelectType;
    private ReceiptDetail loSelectDetail;

    private MaterialTextView mtv_status, btn_download, btn_view, mtv_totalpledge, btn_save_detail;

    private TextInputEditText tie_transaction_no, tie_dtransact, tie_or, tie_remarks, tie_paid_amount,  auto_lodge_member;

    private MaterialAutoCompleteTextView auto_pay_type, auto_source_type, auto_source_transaction;

    private ImageButton btn_add_detail;

    private MaterialButton btn_save;

    private MaterialCardView mcv_ids;
    private ConstraintLayout layout_tools;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__receipt, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Receipts.class);
        laDetails = new ArrayList<>();
        poLoading = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        poLoading.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        if (!lsTransNox.isEmpty()) DownloadReceipt();

        return view;
    }

    private void InitViews(View view){

        mtv_status       = view.findViewById(R.id.mtv_status);
        btn_download     = view.findViewById(R.id.btn_download);
        btn_view         = view.findViewById(R.id.btn_view);
        mtv_totalpledge  = view.findViewById(R.id.mtv_totalpledge);
        btn_save_detail  = view.findViewById(R.id.btn_save_detail);

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        tie_dtransact      = view.findViewById(R.id.tie_dtransact);
        tie_or             = view.findViewById(R.id.tie_or);
        tie_remarks        = view.findViewById(R.id.tie_remarks);
        tie_paid_amount    = view.findViewById(R.id.tie_paid_amount);

        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        auto_pay_type = view.findViewById(R.id.auto_pay_type);
        auto_source_type = view.findViewById(R.id.auto_source_type);
        auto_source_transaction = view.findViewById(R.id.auto_source_transaction);

        btn_add_detail = view.findViewById(R.id.btn_add_detail);
        btn_save = view.findViewById(R.id.btn_save);

        mcv_ids = view.findViewById(R.id.mcv_ids);
        layout_tools = view.findViewById(R.id.layout_tools);

    }

    private void InitDataReceiver(){

        if (getArguments() == null || getArguments().getString("member_id") == null){
            Toast.makeText(requireActivity(), "Member ID is not initialized", Toast.LENGTH_SHORT).show();

            requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, POP_BACK_STACK_INCLUSIVE);
            return;
        }
        lsMemberID = getArguments() == null || getArguments().getString("member_id") == null ? "" : getArguments().getString("member_id");
        lsTransNox = getArguments() == null || getArguments().getString("transaction_id") == null ? "" : getArguments().getString("transaction_id");

        auto_pay_type.setAdapter(new ArrayAdapter<>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mViewModel.GetPaymentTypes()
        ));

        auto_source_type.setAdapter(
                new Adapter_Receipt_Sources(
                    requireActivity(),
                    mViewModel.GetSources()
            )
        );

        mViewModel.ObserveReceiptMaster(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EReceiptMaster>() {
            @Override
            public void onChanged(EReceiptMaster eReceiptMaster) {

                //clear fields
                ClearFields(List.of(auto_lodge_member, tie_or, tie_remarks, auto_source_transaction, tie_paid_amount), true);

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
                            "0", //new entries are considered approved
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate(),
                            mViewModel.GetCurrentDateTime()
                    );
                    mcv_ids.setVisibility(View.GONE);
                    layout_tools.setVisibility(View.GONE);

                }else {

                    loReceipt = eReceiptMaster;

                    mcv_ids.setVisibility(View.VISIBLE);
                    layout_tools.setVisibility(View.VISIBLE);
                }

                //reset selections
                lsSelectType = null;
                loSelectDetail = null;

                //get payment form
                lnSelectPaym = Integer.parseInt(loReceipt.getCPaymForm());

                //display master values
                tie_transaction_no.setText(loReceipt.getSORNoxxxx());
                tie_dtransact.setText(loReceipt.getDTransact());

                switch (loReceipt.getCTranStat()){

                    case "0":
                        mtv_status.setText("Open");
                        break;
                    case "1":
                        mtv_status.setText("Confirmed");
                        break;
                    case "3":
                        mtv_status.setText("Cancelled");
                        break;
                    case "4":
                        mtv_status.setText("Void");
                        break;
                }

                btn_save.setVisibility(loReceipt.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_save_detail.setVisibility(View.GONE);

                auto_lodge_member.setText(mViewModel.GetMemberInfo(lsMemberID).getSFrstName() + " " + mViewModel.GetMemberInfo(lsMemberID).getSLastName());
                auto_pay_type.setText(mViewModel.GetPaymentTypes().get(Integer.parseInt(loReceipt.getCPaymForm())), false);
                tie_or.setText(loReceipt.getSORNoxxxx());
                tie_remarks.setText(loReceipt.getSRemarksx());

                AllowFields(List.of(auto_pay_type, tie_remarks, btn_add_detail, tie_paid_amount), loReceipt.getCTranStat().equalsIgnoreCase("0"));

                //load existing details of record
                mViewModel.ObserveReceiptDetail(lsTransNox).observe(getViewLifecycleOwner(), new Observer<List<EReceiptDetail>>() {
                    @Override
                    public void onChanged(List<EReceiptDetail> eReceiptDetails) {

                        if (eReceiptDetails == null || eReceiptDetails.size() < 1) return;

                        mViewModel.ClearDetail();
                        for (EReceiptDetail loDetail : eReceiptDetails){

                            if (loDetail.getSSourceCD().equalsIgnoreCase("PROJ")){

                                EProjectMaster loProj = mViewModel.GetProjectMaster(loDetail.getSSourceNo());
                                mViewModel.AddReceiptDetail(
                                        loDetail.getSTransNox(),
                                        loDetail.getSSourceCD(),
                                        loDetail.getSSourceNo(),
                                        loDetail.getNEntryNox(),
                                        loProj == null || loProj.getSProjctCd().isEmpty() ? "" : loProj.getSProjctNm(),
                                        loDetail.getNAmtPaidx()
                                );

                            }else if (loDetail.getSSourceCD().equalsIgnoreCase("DUES")){

                                mViewModel.AddReceiptDetail(
                                        loDetail.getSTransNox(),
                                        loDetail.getSSourceCD(),
                                        loDetail.getSSourceNo(),
                                        loDetail.getNEntryNox(),
                                        "Membership Fee",
                                        loDetail.getNAmtPaidx()
                                );

                            }else {

                                mViewModel.AddReceiptDetail(
                                        loDetail.getSTransNox(),
                                        loDetail.getSSourceCD(),
                                        loDetail.getSSourceNo(),
                                        loDetail.getNEntryNox(),
                                        loDetail.getSSourceNo(),
                                        loDetail.getNAmtPaidx()
                                );
                            }
                        }
                        mViewModel.SelectType(eReceiptDetails.get(eReceiptDetails.size() - 1).getSSourceCD());
                    }
                });
            }
        });

        mViewModel.ObserveSelectType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                if (s == null) return;

                mViewModel.ObserveReceiptDetail().observe(getViewLifecycleOwner(), new Observer<List<ReceiptDetail>>() {
                    @Override
                    public void onChanged(List<ReceiptDetail> receiptDetails) {

                        if (receiptDetails == null) return;

                        //collect all transaction details
                        laDetails = receiptDetails;

                        //filter details to display from selected type
                        List<ReceiptDetail> laTransactions = new ArrayList<>();
                        double ldbl_totalpaid = 0.00;
                        for (ReceiptDetail loDetail : laDetails){

                            //if source code match the selected type
                            if (loDetail.getSSourceCD().equalsIgnoreCase(s)){
                                laTransactions.add(loDetail);

                                ldbl_totalpaid += Double.parseDouble(loDetail.getNAmtPaidx());
                            }
                        }
                        mtv_totalpledge.setText(String.valueOf(ldbl_totalpaid));

                        //clear adapter for new list
                        auto_source_transaction.setText("");
                        tie_paid_amount.setText("0.00");

                        auto_source_transaction.setAdapter(null);

                        auto_source_transaction.setAdapter(
                                new Adapter_Receipt_Transactions(
                                        requireActivity(),
                                        R.layout.list_receipt_details,
                                        laTransactions
                                )
                        );

                        mViewModel.GetSources()
                                .stream()
                                .filter(receiptConstants -> receiptConstants.getFsID().equalsIgnoreCase(s))
                                .findAny()
                                .ifPresent(receiptConstants -> auto_source_type.setText(receiptConstants.getFsDescript(), false));

                        laTransactions
                                .stream()
                                .filter(receiptDetail -> receiptDetail.getSSourceCD().equalsIgnoreCase(s))
                                .reduce((first, second) -> second)
                                .ifPresent(receiptDetail -> {

                                    auto_source_transaction.setText(receiptDetail.getSSourceDscrpt(), false);
                                    tie_paid_amount.setText(receiptDetail.getNAmtPaidx());
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

    private void DownloadReceipt(){

        mViewModel.DownloadReceiptInfo(lsTransNox, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Downloading receipt information. Please wait . .");

                AllowFields(List.of(auto_pay_type, tie_remarks, btn_add_detail, tie_paid_amount, btn_save, btn_add_detail), false);
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoading.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                AllowFields(List.of(auto_pay_type, tie_remarks, btn_add_detail, tie_paid_amount, btn_save, btn_add_detail), loReceipt.getCTranStat().equalsIgnoreCase("0"));
            }
        });
    }

    private void InitListener(){

        auto_pay_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_pay_type.getAdapter() == null) return;
                lnSelectPaym = mViewModel.GetPaymentTypes().indexOf((String) auto_pay_type.getAdapter().getItem(i));
            }
        });

        auto_source_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_type.getAdapter() == null) return;

                lsSelectType = mViewModel.GetSources().get(i).getFsID();

                auto_source_type.setText(mViewModel.GetSources().get(i).getFsDescript());
                mViewModel.SelectType(lsSelectType);

                //enable amount field, if open status
                tie_paid_amount.setEnabled(loReceipt.getCTranStat().equalsIgnoreCase("0"));
                btn_save_detail.setVisibility(View.GONE);
            }
        });

        auto_source_transaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_transaction.getAdapter() == null) return;

                loSelectDetail = (ReceiptDetail) auto_source_transaction.getAdapter().getItem(i);

                auto_source_transaction.setText(loSelectDetail.getSSourceDscrpt().isEmpty() ? loSelectDetail.getSSourceNo() : loSelectDetail.getSSourceDscrpt());
                tie_paid_amount.setText(loSelectDetail.getNAmtPaidx());

                btn_save_detail.setVisibility(View.GONE);
            }
        });

        tie_paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() < 1 || loSelectDetail == null){
                    return;
                }
                btn_save_detail.setVisibility(Double.parseDouble(loSelectDetail.getNAmtPaidx()) != Double.parseDouble(charSequence.toString()) ? View.VISIBLE : View.GONE);
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadReceipt();
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Receipt_Detail loReceiptDetail = new Fragment_Receipt_Detail();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                Bundle loBundle = new Bundle();
                loBundle.putString("member_id", lsMemberID);
                loBundle.putString("transaction_id", lsTransNox);

                loReceiptDetail.setArguments(loBundle);

                //add to child container, as bridge to initiate another fragment after call
                fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loReceiptDetail);
                fragmentTransaction.addToBackStack(lsStackIDxx);
                fragmentTransaction.commit();
            }
        });

        btn_add_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog_Receipt_Entry loReceipt = new Dialog_Receipt_Entry(
                        requireActivity(),
                        lsMemberID,
                        mViewModel,
                        getViewLifecycleOwner(),
                        new Dialog_Receipt_Entry.OnSubmitEntry() {
                            @Override
                            public void OnSubmit(ReceiptDetail loEntry) {

                                if (!mViewModel.AddReceiptDetail(
                                        "",
                                        loEntry.getSSourceCD(),
                                        loEntry.getSSourceNo(),
                                        "",
                                        loEntry.getSSourceDscrpt(),
                                        loEntry.getNAmtPaidx()
                                )){
                                    Toast.makeText(requireActivity(), "Transaction is already added", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                );
                loReceipt.Show();
            }
        });

        btn_save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectDetail == null || laDetails.indexOf(loSelectDetail) < 0) {
                    Toast.makeText(requireActivity(), "Please select a detail", Toast.LENGTH_SHORT).show();
                    return;
                }
                mViewModel.ReplaceDetail(laDetails.indexOf(loSelectDetail), tie_paid_amount.getText().toString());

                view.setVisibility(View.GONE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loReceipt == null){
                    Toast.makeText(requireActivity(), "Receipt information is not initailized", Toast.LENGTH_SHORT).show();
                    return;
                } else if (laDetails == null || laDetails.size() < 1) {
                    Toast.makeText(requireActivity(), "Please enter atleast one detail", Toast.LENGTH_SHORT).show();
                    return;
                }

                //initialize primary receipt details
                loReceipt.setCPaymForm(String.valueOf(lnSelectPaym));
                loReceipt.setSORNoxxxx(tie_or.getText() == null ? "" : tie_or.getText().toString());
                loReceipt.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());

               poMessage.ShowMessage(2, "Do you want to save receipt information?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                   @Override
                   public void OnPositive(@NotNull AlertDialog poDialog) {
                       poDialog.dismiss();
                   }

                   @Override
                   public void OnNegative(@NotNull AlertDialog poDialog) {
                       poDialog.dismiss();

                       mViewModel.SaveReceipt(loReceipt, laDetails, new VM_Receipts.OnSaveReceipt() {
                           @Override
                           public void OnLoad() {
                               poLoading.ShowDialog("Saving receipt. Please wait . .");
                           }

                           @Override
                           public void OnSuccess(String loTransNox) {
                               poLoading.DismissDialog();

                               poMessage.ShowMessage(0, "Receipt has been saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                   @Override
                                   public void OnPositive(@NotNull AlertDialog poDialog) {
                                       poDialog.dismiss();

                                       //reload entry
                                       lsTransNox = loTransNox;

                                       //open receipt information to share
                                       Fragment_Receipt_Detail loReceiptDetail = new Fragment_Receipt_Detail();
                                       FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                                       Bundle loBundle = new Bundle();
                                       loBundle.putString("member_id", lsMemberID);
                                       loBundle.putString("transaction_id", lsTransNox);

                                       loReceiptDetail.setArguments(loBundle);

                                       //add to child container, as bridge to initiate another fragment after call
                                       fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loReceiptDetail);
                                       fragmentTransaction.addToBackStack(lsStackIDxx);
                                       fragmentTransaction.commit();
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
                                   public void OnPositive(@NotNull AlertDialog poDialog) {
                                       poDialog.dismiss();
                                       requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, POP_BACK_STACK_INCLUSIVE);
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
}