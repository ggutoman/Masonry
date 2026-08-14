package com.gag.accounting.Fragments.Fund;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gag.accounting.Adapter.Fund.Adapter_Fund_Ledger;
import com.gag.accounting.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.ViewModels.VM_Lodge;
import org.gag.appdriver.Room.Entities.EFundLedger;
import org.gag.appdriver.Room.Entities.ELodgeInfo;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Lodge_Fund extends Fragment {

    private VM_Lodge mViewmodel;
    private LoadDialog poLoading;
    private Message_Dialog poMessage;
    private Adapter_Fund_Ledger loAdapter;

    private String lsDfrom, lsDto;

    private ConstraintLayout layout_no_record;
    private MaterialCardView layout_ledger;
    private ShapeableImageView btn_download;
    private MaterialTextView mtv_lodge_name;
    private MaterialTextView mtv_lodge_address;
    private MaterialTextView mtv_beginningdate;
    private MaterialTextView mtv_begbal, mtv_endbal;

    // RecyclerView
    private RecyclerView rcv_fund_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_lodge_fund, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Lodge.class);
        poLoading = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        String lsCurrentYear = mViewmodel.GetFormatDate(mViewmodel.GetCurrentDate(), "yyyy");

        //initialize default range of date to display for the whole year
        lsDfrom = mViewmodel.GetFormatDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mViewmodel.GetFormatDate(lsCurrentYear + "-12-30", "yyyy-MM-dd");

        poMessage.InitDialog();
        poLoading.InitDialog();

        InitViews(view);
        InitDataReceover();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        btn_download = view.findViewById(R.id.btn_download);

        layout_no_record = view.findViewById(R.id.layout_no_record);
        layout_ledger = view.findViewById(R.id.layout_ledger);

        mtv_lodge_name = view.findViewById(R.id.mtv_lodge_name);
        mtv_lodge_address = view.findViewById(R.id.mtv_lodge_address);
        mtv_beginningdate = view.findViewById(R.id.mtv_beginningdate);
        mtv_begbal = view.findViewById(R.id.mtv_begbal);
        mtv_endbal = view.findViewById(R.id.mtv_endbal);

        rcv_fund_list = view.findViewById(R.id.rcv_fund_list);

    }

    private void InitDataReceover(){

        if (getArguments() == null){
            Toast.makeText(requireActivity(), "Could not find lodge fund to load", Toast.LENGTH_SHORT).show();
            return;
        }

        ELodgeInfo loLodgeInfo = mViewmodel.GetLodgeInfo(getArguments().getString("lodge_id"));
        if (loLodgeInfo == null){
            Toast.makeText(requireActivity(), "Could not find lodge info to load", Toast.LENGTH_SHORT).show();
            return;
        }

        mtv_lodge_name.setText(loLodgeInfo.getSLodgeNme());

        String lsAddress = loLodgeInfo.getSAddressx() + ", " + loLodgeInfo.getSTownName() + ", " + loLodgeInfo.getSProvName();
        mtv_lodge_address.setText(lsAddress);

        mViewmodel.GetFundLedger(getArguments().getString("lodge_id"), lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EFundLedger>>() {
            @Override
            public void onChanged(List<EFundLedger> eFundLedgers) {

                //display date to compute
                mtv_beginningdate.setText("Lodge Fund as of " + mViewmodel.GetFormatDate(lsDto, "MMMM d, yyyy"));

                if (eFundLedgers == null || eFundLedgers.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    layout_ledger.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                layout_ledger.setVisibility(View.VISIBLE);

                double begbal = 0.00;
                double endbal = 0.00;
                for (EFundLedger loItem : eFundLedgers){

                    //sum up all amount in (paid) and out (due)
                    double ldblAmtIn = Double.parseDouble(loItem.getNAmountIn() == null ? "0.00" : loItem.getNAmountIn());
                    double ldblAmtOut = Double.parseDouble(loItem.getNAmountOt() == null ? "0.00" : loItem.getNAmountOt());

                    //if transact date is before the specified date, compute beginning balance & ending balance
                    if (mViewmodel.IsDateCompared(loItem.getDTransact(), lsDto)){

                        //add to total beg balance
                        begbal += Math.abs(ldblAmtIn - ldblAmtOut);
                    }

                    if (loItem.getDTransact().equals(lsDto)) endbal += begbal + Math.abs(ldblAmtIn - ldblAmtOut);
                }

                //display computed balance
                mtv_begbal.setText(String.valueOf(begbal));
                mtv_endbal.setText(String.valueOf(endbal));

                loAdapter = new Adapter_Fund_Ledger(eFundLedgers);

                rcv_fund_list.setAdapter(loAdapter);
                rcv_fund_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    private void InitListener(){

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                loBuilder.setTitleText("Select Date Range");

                MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        //set date range parameters for downloading history
                        lsDfrom = mViewmodel.GetFormattedDate(selection.first);
                        lsDto = mViewmodel.GetFormattedDate(selection.second);

                        poMessage.ShowMessage(2, "Do you want to download funds within " + lsDfrom + " to " + lsDto + "?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                //filter list by date
                                DownloadFunds(lsDfrom, lsDto);
                            }
                        });
                    }
                });
                loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");
            }
        });
    }

    private void DownloadFunds(String fsDfrom, String fsDto){

        mViewmodel.DownloadLodgeFunds(fsDfrom, fsDto, new VM_Lodge.OnDownload() {
            @Override
            public void OnLoad() {
                poLoading.ShowDialog("Downloading Funds. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoading.DismissDialog();

                Toast.makeText(requireActivity(), "Funds downloaded successfully", Toast.LENGTH_SHORT).show();
                InitDataReceover();
            }

            @Override
            public void OnError(String fsMessage) {
                poLoading.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //still load the records
                        InitDataReceover();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }
                });

            }
        });
    }
}