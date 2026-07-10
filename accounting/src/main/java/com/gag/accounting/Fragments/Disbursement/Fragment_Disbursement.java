package com.gag.accounting.Fragments.Disbursement;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_Sources;
import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_Transactions;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Disbursement;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeAdapter;
import org.gag.appdriver.App.Models.DisbursementDetail;
import org.gag.appdriver.App.Models.LodgeFundMasterList;
import org.gag.appdriver.App.Models.LodgeInfo;
import org.gag.appdriver.Constants.DISBURSEMENT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Disbursement extends Fragment {

    private VM_Disbursement mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;
    private EDisbursement loDisbursement;

    private String lsStackIDxx, lsTransNox;
    private LodgeInfo loSelectLodge;
    private DISBURSEMENT_CONSTANTS loSelectType;
    private DisbursementDetail loSelectTransaction;

    private MaterialTextView mtv_status, btn_download, btn_download_sources;
    private TextInputLayout til_source_type, til_source_no, til_lodge;
    private TextInputEditText tie_transaction_no, tie_dtransact, tie_payee, tie_particulars, tie_voucher, tie_amount;
    private MaterialAutoCompleteTextView auto_source_type, auto_source_no, auto_lodge;
    private ConstraintLayout layout_tools;
    private LinearLayout layout_buttons;
    private MaterialButton btn_save;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__disbursement, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Disbursement.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        poLoad.InitDialog();
        poMessage.InitDialog();

        lsTransNox = getArguments() == null || getArguments().getString("transaction_id") == null ? "" : getArguments().getString("transaction_id");

        if (!lsTransNox.isEmpty()) DownloadDisbursement();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){
        mtv_status = view.findViewById(R.id.mtv_status);
        btn_download = view.findViewById(R.id.btn_download);
        btn_download_sources= view.findViewById(R.id.btn_download_source);

        til_lodge = view.findViewById(R.id.til_lodge);
        til_source_type = view.findViewById(R.id.til_source_type);
        til_source_no = view.findViewById(R.id.til_source_no);

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        tie_dtransact = view.findViewById(R.id.tie_dtransact);
        tie_payee = view.findViewById(R.id.tie_payee);
        tie_particulars = view.findViewById(R.id.tie_particulars);
        tie_voucher = view.findViewById(R.id.tie_voucher);
        tie_amount = view.findViewById(R.id.tie_amount);

        auto_lodge = view.findViewById(R.id.auto_lodge);
        auto_source_type = view.findViewById(R.id.auto_source_type);
        auto_source_no = view.findViewById(R.id.auto_source_no);

        layout_tools = view.findViewById(R.id.layout_tools);
        layout_buttons = view.findViewById(R.id.layout_buttons);

        btn_save = view.findViewById(R.id.btn_save);
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

    private void InitDataReceiver(){

        auto_source_type.setAdapter(new Adapter_Disbursement_Sources(
                requireContext(),
                mViewModel.GetSources()
        ));

        mViewModel.ObserveLodgeList().observe(getViewLifecycleOwner(), new Observer<List<LodgeInfo>>() {
            @Override
            public void onChanged(List<LodgeInfo> lodgeInfos) {

                if (lodgeInfos == null) return;

                auto_lodge.setAdapter(new LodgeAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        lodgeInfos
                ));

                //initialize default selection of lodge
                if (loSelectLodge == null){

                    for (LodgeInfo loLodge : lodgeInfos){

                        if (loLodge.getSLodgeIDx().equalsIgnoreCase(mViewModel.GetLodgeID())){
                            loSelectLodge = loLodge;
                            auto_lodge.setText(loSelectLodge.getSLodgeNme(), false);
                            break;
                        }

                    }

                }
            }
        });

        mViewModel.GetDisbursement(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EDisbursement>() {
            @Override
            public void onChanged(EDisbursement eDisbursement) {

                if (eDisbursement == null){

                    loDisbursement = new EDisbursement(
                            "",
                            mViewModel.GetCurrentDate(),
                            "FND",
                            "",
                            "",
                            "",
                            "",
                            "0.00",
                            "0",
                            "",
                            "",
                            "",
                            "",
                            mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate()
                    );

                    btn_download.setVisibility(View.GONE);
                }else {
                    loDisbursement = eDisbursement;

                    btn_download.setVisibility(View.VISIBLE);
                }

                ClearFields(List.of(tie_transaction_no, tie_dtransact, tie_payee, tie_particulars, tie_voucher, auto_source_no, tie_amount), true);

                tie_transaction_no.setText(loDisbursement.getSTransNox());
                tie_dtransact.setText(loDisbursement.getDTransact());
                tie_payee.setText(loDisbursement.getSPayeeNme());
                tie_particulars.setText(loDisbursement.getSParticlr());
                tie_voucher.setText(loDisbursement.getSVouchrNo());
                auto_source_no.setText(loDisbursement.getSSourceNo(), false);
                tie_amount.setText(loDisbursement.getNAmountxx());

                //load source type on selection
                for (DISBURSEMENT_CONSTANTS loSrcTypes : mViewModel.GetSources()){

                    if (loDisbursement.getSSourceCD().equalsIgnoreCase(loSrcTypes.getFsID())){
                        auto_source_type.setText(loSrcTypes.getFsDescript());
                        mViewModel.SelectType(loDisbursement.getSSourceCD());

                        loSelectType = loSrcTypes;
                        break;
                    }

                }

                switch (loDisbursement.getCTranStat()){

                    case "0":
                        mtv_status.setText("Open");
                        break;
                    case "1":
                        mtv_status.setText("Confirmed");
                        break;
                    case "2":
                        mtv_status.setText("Released");
                        break;
                    case "3":
                        mtv_status.setText("Cancelled");
                        break;
                    case "4":
                        mtv_status.setText("Void");
                        break;
                    case "5":
                        mtv_status.setText("Completed");
                        break;
                }
                til_lodge.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_save.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_download_sources.setVisibility(loDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);

                AllowFields(List.of(tie_payee, tie_particulars, tie_voucher, til_source_type, til_source_no, tie_amount), loDisbursement.getCTranStat().equals("0"));
            }
        });

        mViewModel.ObserveSelectType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                switch (s){

                    case "FND":

                        mViewModel.GetLodgeFunds(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx()).observe(getViewLifecycleOwner(), new Observer<List<LodgeFundMasterList>>() {
                            @Override
                            public void onChanged(List<LodgeFundMasterList> lodgeFundMasterLists) {

                                if (lodgeFundMasterLists == null) return;

                                List<DisbursementDetail> laDetails = new ArrayList<>();
                                for (LodgeFundMasterList loFund : lodgeFundMasterLists){

                                    DisbursementDetail loDisb = new DisbursementDetail(
                                            "FND",
                                            loFund.getSLodgeNme(),
                                            loFund.getSLodgeIDx(),
                                            loFund.getNBalancex()
                                    );

                                    //add to list
                                    laDetails.add(loDisb);

                                    //select transaction if matched the entry source no
                                    if (loFund.getSLodgeIDx().equalsIgnoreCase(loDisbursement.getSSourceNo())){
                                        loSelectTransaction = loDisb;
                                    }
                                }

                                auto_source_no.setAdapter(
                                        new Adapter_Disbursement_Transactions(
                                                requireContext(),
                                                R.layout.list_disbursement_details,
                                                laDetails
                                        )
                                );
                            }
                        });
                        break;
                    case "PROJ":

                        mViewModel.GetLodgeProjects(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx()).observe(getViewLifecycleOwner(), new Observer<List<EProjectMaster>>() {
                            @Override
                            public void onChanged(List<EProjectMaster> eProjectMasters) {

                                if (eProjectMasters == null) return;

                                List<DisbursementDetail> laDetails = new ArrayList<>();
                                for (EProjectMaster loProject : eProjectMasters){

                                    DisbursementDetail loDisb = new DisbursementDetail(
                                            "PROJ",
                                            loProject.getSProjctNm(),
                                            loProject.getSProjctCd(),
                                            loProject.getNCollTotl()
                                    );

                                    //add to list
                                    laDetails.add(loDisb);

                                    //select transaction if matched the entry source no
                                    if (loProject.getSProjctCd().equalsIgnoreCase(loDisbursement.getSSourceNo())){
                                        loSelectTransaction = loDisb;
                                    }
                                }

                                auto_source_no.setAdapter(
                                        new Adapter_Disbursement_Transactions(
                                                requireContext(),
                                                R.layout.list_disbursement_details,
                                                laDetails
                                        )
                                );
                            }
                        });
                        break;
                }
            }
        });
    }

    private void DownloadDisbursement(){

        mViewModel.DownloadDisbursementInfo(lsTransNox, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading disbursement. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DownloadProjects(){

        mViewModel.DownloadProjects(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx(), new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading projects. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                mViewModel.SelectType(loSelectType.getFsID());
            }
        });
    }

    private void DownloadLodgeFunds(){

        mViewModel.DownloadLodgeFunds(loSelectLodge == null ? "" : loSelectLodge.getSLodgeIDx(), new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading lodge funds. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                mViewModel.SelectType(loSelectType.getFsID());
            }
        });
    }

    private void DownloadSources(){

        if (loSelectType == null){
            Toast.makeText(requireActivity(), "Please select source type", Toast.LENGTH_SHORT).show();
            return;
        } else if (loSelectLodge == null) {
            Toast.makeText(requireActivity(), "Please select a lodge first", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (loSelectType.getFsID()){

            case "FND":
                DownloadLodgeFunds();
                break;
            case "PROJ":
                DownloadProjects();
                break;

        }
    }

    private void InitListener(){

        auto_lodge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_lodge.getAdapter() == null) return;
                loSelectLodge = (LodgeInfo) auto_lodge.getAdapter().getItem(i);

                auto_lodge.setText(loSelectLodge.getSLodgeNme(), false);
            }
        });

        auto_source_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_type.getAdapter() == null) return;
                loSelectType = (DISBURSEMENT_CONSTANTS) auto_source_type.getAdapter().getItem(i);

                auto_source_type.setText(loSelectType.getFsDescript(), false);

                //clear voucher fields
                ClearFields(List.of(auto_source_no, tie_amount), true);

                //re initialize transaction list
                mViewModel.SelectType(loSelectType.getFsID());
            }
        });

        auto_source_no.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (auto_source_no.getAdapter() == null) return;
                loSelectTransaction = (DisbursementDetail) auto_source_no.getAdapter().getItem(i);

                auto_source_no.setText(loSelectTransaction.getSSourceNme(), false);
                tie_amount.setText(loSelectTransaction.getNDisbAmt());
            }
        });

        btn_download_sources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadSources();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownloadDisbursement();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        if (tie_payee.getText() == null || tie_payee.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter payee name", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (tie_particulars.getText() == null || tie_particulars.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter particulars", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (tie_voucher.getText() == null || tie_voucher.getText().toString().isEmpty()){
                            Toast.makeText(requireActivity(), "Please enter voucher", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loDisbursement == null){
                            Toast.makeText(requireActivity(), "Disbursement is not initialized properly", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loSelectType == null){
                            Toast.makeText(requireActivity(), "Please select source type", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (loSelectTransaction == null){
                            Toast.makeText(requireActivity(), "Please select source transaction", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //initialize voucher information
                        loDisbursement.setSPayeeNme(tie_payee.getText() == null ? "" : tie_payee.getText().toString());
                        loDisbursement.setSParticlr(tie_particulars.getText() == null ? "" : tie_particulars.getText().toString());
                        loDisbursement.setSVouchrNo(tie_voucher.getText() == null ? "" : tie_voucher.getText().toString());
                        loDisbursement.setSSourceCD(loSelectType.getFsID());
                        loDisbursement.setSSourceNo(loSelectTransaction.getSSourceNo());
                        loDisbursement.setNAmountxx(tie_amount.getText() == null ? "" : tie_amount.getText().toString());

                        mViewModel.SaveDisbursement(loDisbursement, new VM_Disbursement.OnTransaction() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Saving disbursement. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();
                                Toast.makeText(requireActivity(), "Disbursement saved successfully", Toast.LENGTH_SHORT).show();

                                requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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