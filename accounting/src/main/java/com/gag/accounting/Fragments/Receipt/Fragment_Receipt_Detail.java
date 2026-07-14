package com.gag.accounting.Fragments.Receipt;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gag.accounting.Adapter.Receipt.Adapter_Receipt_Details;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptDetail;
import org.gag.appdriver.Room.Entities.EReceiptMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Receipt_Detail extends Fragment {

    private VM_Receipts mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private EReceiptMaster loReceipt;
    private List<EReceiptDetail> loReceiptDetail;

    private String lsStackIDxx, lsMemberID, lsTransNox;

    private View layoutView;
    private MaterialTextView mtv_transno;
    private MaterialTextView mtv_transdate;
    private MaterialTextView mtv_member;
    private MaterialTextView mtv_remarks;
    private MaterialTextView mtv_payment_total;
    private MaterialTextView mtv_payment;
    private MaterialTextView mtv_or;

    private ConstraintLayout layout_no_record;
    private LinearLayout layout_buttons;

    private MaterialButton btn_confirm, btn_void, btn_cancel, btn_share;

    private RecyclerView rcv_list;

    private final ActivityResultLauncher<Intent> shareLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                //some apps will always return cancelled, so sent receipts is not validated and is open to update whether cancelled or not
                if (loReceipt == null) return;

                //initialize cSendStat to sent, and update record
                loReceipt.setCSendStat("1");

                mViewModel.UpdateReceiptStatus(loReceipt, new VM_Receipts.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Saving receipt. Please wait. .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        //reload transaction, to display share button after approving
                        InitDataReceiver();
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layoutView = inflater.inflate(R.layout.fragment__confirm__receipt, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Receipts.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(layoutView);
        InitDataReceiver();
        InitListener();

        DownloadList();

        return layoutView;
    }

    private void InitViews(View view){
        mtv_transno = view.findViewById(R.id.mtv_transno);
        mtv_transdate = view.findViewById(R.id.mtv_transdate);
        mtv_member = view.findViewById(R.id.mtv_member);
        mtv_remarks = view.findViewById(R.id.mtv_remarks);
        mtv_payment_total = view.findViewById(R.id.mtv_payment_total);
        mtv_payment = view.findViewById(R.id.mtv_payment);
        mtv_or = view.findViewById(R.id.mtv_or);

        layout_no_record = view.findViewById(R.id.layout_no_record);
        layout_buttons = view.findViewById(R.id.layout_buttons);

        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_void = view.findViewById(R.id.btn_void);
        btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_share = view.findViewById(R.id.btn_share);

        rcv_list = view.findViewById(R.id.tcv_list);


    }

    private void InitDataReceiver(){

        lsMemberID = getArguments() == null || getArguments().getString("member_id") == null ? "" : getArguments().getString("member_id");
        lsTransNox = getArguments() == null || getArguments().getString("transaction_id") == null ? "" : getArguments().getString("transaction_id");

        mViewModel.ObserveReceiptMaster(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EReceiptMaster>() {
            @Override
            public void onChanged(EReceiptMaster eReceiptMaster) {

                if (eReceiptMaster == null) return;

                loReceipt = eReceiptMaster;

                mtv_transno.setText(loReceipt.getSTransNox());
                mtv_transdate.setText(mViewModel.GetFormattedDate(loReceipt.getDTransact(), "MMMM dd, yyyy"));
                mtv_member.setText(mViewModel.GetMemberInfo(lsMemberID).getSFrstName() + " " + mViewModel.GetMemberInfo(lsMemberID).getSLastName());
                mtv_remarks.setText(loReceipt.getSRemarksx() == null || eReceiptMaster.getSRemarksx().isEmpty() ? "No remarks" : loReceipt.getSRemarksx());
                mtv_or.setText(loReceipt.getSORNoxxxx());

                //display buttons based on status
                layout_buttons.setVisibility(lsStackIDxx.equalsIgnoreCase("receipt_confirm")? View.VISIBLE : View.GONE);
                btn_share.setVisibility(lsStackIDxx.equalsIgnoreCase("receipt_confirm") && loReceipt.getCTranStat().equalsIgnoreCase("1") && loReceipt.getCSendStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);

                btn_confirm.setVisibility(loReceipt.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_void.setVisibility(loReceipt.getCTranStat().equalsIgnoreCase("0") ? View.VISIBLE : View.GONE);
                btn_cancel.setVisibility(loReceipt.getCTranStat().equalsIgnoreCase("1") ? View.VISIBLE : View.GONE);

                switch (loReceipt.getCPaymForm()){

                    case "0":
                        mtv_payment.setText("Cash Payment");
                        break;
                    case "1":
                        mtv_payment.setText("Check Payment");
                        break;

                    case "2":
                        mtv_payment.setText("Bank Transfer");
                        break;
                }

                mViewModel.ObserveReceiptDetail(lsTransNox).observe(getViewLifecycleOwner(), new Observer<List<EReceiptDetail>>() {
                    @Override
                    public void onChanged(List<EReceiptDetail> eReceiptDetails) {

                        if (eReceiptDetails == null || eReceiptDetails.size() < 1){
                            layout_no_record.setVisibility(View.VISIBLE);
                            rcv_list.setVisibility(View.GONE);
                            return;
                        }
                        layout_no_record.setVisibility(View.GONE);
                        rcv_list.setVisibility(View.VISIBLE);

                        loReceiptDetail = eReceiptDetails;

                        double ldbl_totalpaid = 0.00;
                        for (EReceiptDetail loDetail : eReceiptDetails){
                            ldbl_totalpaid += Double.parseDouble(loDetail.getNAmtPaidx());
                        }

                        mtv_payment_total.setText("₱ "+ String.valueOf(ldbl_totalpaid));

                        rcv_list.setAdapter(
                                new Adapter_Receipt_Details(
                                        requireActivity(),
                                        eReceiptDetails
                                )
                        );
                        rcv_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                    }
                });
            }
        });
    }

    private void ShareReceipt(File file) {

        try {

            Uri uri = FileProvider.getUriForFile(
                    requireActivity(),
                    requireActivity().getPackageName() + ".provider",
                    file
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            shareLauncher.launch(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("File Path", file.getAbsolutePath());
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateStatus(String fsTranStat){

        String lsConfirm = "";
        switch (fsTranStat){

            case "1":
                lsConfirm = "confirm this transaction?";
                break;
            case "3":
                lsConfirm = "cancel this transaction?";
                break;
            case "4":
                lsConfirm = "void this transaction?";
                break;
        }

        poMessage.ShowMessage(2, "Are you sure you want to " + lsConfirm, "No", "Yes", new Message_Dialog.OnDialogClick() {
            @Override
            public void OnPositive(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();
            }

            @Override
            public void OnNegative(@NotNull AlertDialog poDialog) {
                poDialog.dismiss();

                //initialize status first, then update
                loReceipt.setCTranStat(fsTranStat);
                mViewModel.UpdateReceiptStatus(loReceipt, new VM_Receipts.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Confirming receipt. Please wait. .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        //reload transaction, to display share button after approving
                        InitDataReceiver();
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();
                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void DownloadList(){

        mViewModel.DownloadReceiptInfo(lsTransNox, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading receipt information. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitListener(){

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateStatus("1");
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateStatus("3");
            }
        });

        btn_void.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UpdateStatus("4");
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewModel.CreateFileFromView(layoutView, lsTransNox + ".png", new VM_Receipts.OnShareFile() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Generating receipt. Please wait. .");
                        view.setVisibility(View.GONE);
                        btn_cancel.setVisibility(View.GONE);

                    }

                    @Override
                    public void OnSuccess(File file) {
                        poLoad.DismissDialog();

                        view.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.VISIBLE);

                        ShareReceipt(file);
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();

                        view.setVisibility(View.VISIBLE);
                        btn_cancel.setVisibility(View.VISIBLE);

                        Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}