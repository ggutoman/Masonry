package com.gag.accounting.Fragments.Disbursement;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Disbursement;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.DataModels.DownloadDisbursement;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

public class Fragment_Disbursement_Detail extends Fragment {

    private VM_Disbursement mViewmodel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private EDisbursement loDisb;
    private String lsTransNox, lsMessage, lsStackIDxx;

    private LinearLayout layout_buttons;

    private MaterialTextView
            mtv_name,
            mtv_transno,
            mtv_date,
            mtv_src_no,
            mtv_src_cd,
            mtv_voucher_amt,
            mtv_voucher_no,
            mtv_partclr;

    private MaterialButton btn_confirm,
            btn_void,
            btn_complete,
            btn_cancel,
            btn_release;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__disbursement__status, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Disbursement.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        poLoad.InitDialog();
        poMessage.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        layout_buttons = view.findViewById(R.id.layout_buttons);

        mtv_name         = view.findViewById(R.id.mtv_name);
        mtv_transno      = view.findViewById(R.id.mtv_transno);
        mtv_date         = view.findViewById(R.id.mtv_date);
        mtv_src_no       = view.findViewById(R.id.mtv_src_no);
        mtv_src_cd       = view.findViewById(R.id.mtv_src_cd);
        mtv_voucher_amt  = view.findViewById(R.id.mtv_voucher_amt);
        mtv_voucher_no   = view.findViewById(R.id.mtv_voucher_no);
        mtv_partclr      = view.findViewById(R.id.mtv_partclr);
        btn_confirm      = view.findViewById(R.id.btn_confirm);
        btn_void         = view.findViewById(R.id.btn_void);
        btn_complete     = view.findViewById(R.id.btn_complete);
        btn_cancel       = view.findViewById(R.id.btn_cancel);
        btn_release      = view.findViewById(R.id.btn_release);


    }

    private void InitDataReceiver(){

        lsTransNox = getArguments() == null ? "" : getArguments().getString("transaction_id");

        mViewmodel.GetDisbursement(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EDisbursement>() {
            @Override
            public void onChanged(EDisbursement eDisbursement) {

                if (eDisbursement == null){
                    Toast.makeText(requireActivity(), "Transaction id is empty", Toast.LENGTH_SHORT).show();
                    layout_buttons.setVisibility(View.GONE);
                    return;
                }
                layout_buttons.setVisibility(lsStackIDxx.equalsIgnoreCase("disbursement_confirm") ? View.VISIBLE : View.GONE);
                btn_confirm.setVisibility(eDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_void.setVisibility(eDisbursement.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_cancel.setVisibility(eDisbursement.getCTranStat().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
                btn_release.setVisibility(eDisbursement.getCTranStat().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);
                btn_complete.setVisibility(eDisbursement.getCTranStat().equalsIgnoreCase("2") ? View.VISIBLE : View.GONE);

                loDisb = eDisbursement;

                mtv_name.setText(loDisb.getSPayeeNme());
                mtv_transno.setText(loDisb.getSTransNox());
                mtv_date.setText(loDisb.getDTransact());
                mtv_voucher_amt.setText("₱" + loDisb.getNAmountxx());
                mtv_voucher_no.setText(loDisb.getSVouchrNo());
                mtv_partclr.setText(loDisb.getSParticlr());
                mtv_src_no.setText(loDisb.getSSourceNo());

                switch (loDisb.getSSourceCD()){

                    case "FND":
                        mtv_src_cd.setText("Lodge Fund");
                        break;
                    case "PROJ":
                        mtv_src_cd.setText("Project");
                        break;
                }
            }
        });
    }

    private void UpdateStatus(String fcTranStat){

        if (loDisb == null){
            Toast.makeText(requireActivity(), "Disbursement is not initialzed porperly", Toast.LENGTH_SHORT).show();
            return;
        } else if (fcTranStat == null || fcTranStat.isEmpty()) {
            Toast.makeText(requireActivity(), "Status is invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        loDisb.setCTranStat(fcTranStat);
        loDisb.setSModified(mViewmodel.GetUserID());
        loDisb.setDModified(mViewmodel.GetCurrentDate());

        switch (loDisb.getCTranStat()){

            case "1":
                lsMessage = "confirm disbursement";
                loDisb.setSApproved(mViewmodel.GetUserID());
                loDisb.setDApproved(mViewmodel.GetCurrentDate());
                break;
            case "2":
                lsMessage = "release disbursement";
                loDisb.setDReleased(mViewmodel.GetCurrentDate());
                break;
            case "3":
                lsMessage = "cancel disbursement";
                break;
            case "4":
                lsMessage = "void disbursement";
                break;
            case "5":
                lsMessage = "complete disbursement";
                loDisb.setDComplete(mViewmodel.GetCurrentDate());
                break;
        }

        poMessage.ShowMessage(2, "Are you sure to " + lsMessage + "?", "No", "Yes", new Message_Dialog.OnDialogClick() {
            @Override
            public void OnPositive(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();
            }

            @Override
            public void OnNegative(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();

                mViewmodel.UpdateStatus(loDisb, new VM_Disbursement.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Saving status. Please wait. .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(0, "Disbursement status has been updated " + " successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                requireActivity().getSupportFragmentManager().popBackStack(lsStackIDxx, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                mViewmodel.DownloadDisbursementInfo(loDisb.getSTransNox(), new VM_Receipts.OnDownload() {
                                    @Override
                                    public void OnLoad() {
                                        Toast.makeText(requireActivity(), "Downloading disbursement. Please wait . .", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void OnFinished(String fsMessage) {
                                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {}
                        });
                    }
                });
            }
        });
    }

    private void InitListener(){

        btn_confirm.setOnClickListener(view -> UpdateStatus("1"));
        btn_release.setOnClickListener(view -> UpdateStatus("2"));
        btn_cancel.setOnClickListener(view -> UpdateStatus("3"));
        btn_void.setOnClickListener(view -> UpdateStatus("4"));
        btn_complete.setOnClickListener(view -> UpdateStatus("5"));
    }
}