package com.gag.accounting.Fragments.Annual;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gag.accounting.Adapter.Annual.Adapter_Annual_Members;
import com.gag.accounting.Adapter.Annual.Adapter_Annual_Summary;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.Room.Entities.EAnnualMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Fragment_Annual_Summary extends Fragment {

    private VM_Annual mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsYearIDxx, lsLodgeIDxx, lsDfrom, lsDto;

    private MaterialTextView mtv_lodge_name, mtv_lodge_year, mtv_due_date, mtv_transaction, mtv_trans, mtv_collect;
    private ShapeableImageView btn_download;
    private ConstraintLayout layout_no_record;

    private MaterialCardView layout_ledger;
    private RecyclerView rcv_fund_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_annual_summary, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Annual.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        String lsCurrentYear = mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "yyyy");

        //initialize default range of date to display for the whole year
        lsDfrom = mViewModel.GetFormattedDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mViewModel.GetFormattedDate(lsCurrentYear + "-12-30", "yyyy-MM-dd");

        //initialize parameters for this transaction
        lsYearIDxx = (getArguments() == null || getArguments().getString("year_id") == null) ? "" : getArguments().getString("year_id");
        lsLodgeIDxx = (getArguments() == null || getArguments().getString("lodge_id") == null) ? "" : getArguments().getString("lodge_id");

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void DownloadInfo(){

        if (getArguments() == null) return;
        mViewModel.DownloadAnnual(lsLodgeIDxx, lsYearIDxx, lsDfrom, lsDto, new VM_Annual.OnTransaction() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading annual billing. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();

                Toast.makeText(requireActivity(), "Annual billing downloaded successfully", Toast.LENGTH_SHORT).show();
                InitDataReceiver();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //reload data, after error
                        InitDataReceiver();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });

    }

    private void InitViews(View view){

        mtv_lodge_name = view.findViewById(R.id.mtv_lodge_name);
        mtv_lodge_year = view.findViewById(R.id.mtv_lodge_year);
        mtv_due_date = view.findViewById(R.id.mtv_due_date);
        btn_download = view.findViewById(R.id.btn_download);
        mtv_transaction = view.findViewById(R.id.mtv_transaction);
        mtv_trans = view.findViewById(R.id.mtv_trans);
        mtv_collect = view.findViewById(R.id.mtv_collect);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        layout_ledger = view.findViewById(R.id.layout_ledger);
        rcv_fund_list = view.findViewById(R.id.rcv_fund_list);


    }

    private void InitDataReceiver(){

        //get year id information
        mViewModel.GetLodgeCalendars(lsLodgeIDxx).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null) return;

                //display based on passed parameter
                if (!lsYearIDxx.isEmpty()){

                    for (LodgeCalendarList loItem : lodgeCalendarLists){

                        if (loItem.getSYearIDxx().equalsIgnoreCase(lsYearIDxx)){
                            mtv_lodge_name.setText(loItem.getSLodgeNme());
                            mtv_lodge_year.setText("Year " + loItem.getNYearxxxx());
                        }

                    }

                    //display summary of members from year id
                    mViewModel.GetAnnualMaster(lsYearIDxx).observe(getViewLifecycleOwner(), new Observer<EAnnualMaster>() {
                        @Override
                        public void onChanged(EAnnualMaster eAnnualMaster) {

                            if (eAnnualMaster == null) return;

                            mtv_transaction.setText(eAnnualMaster.getSTransNox());
                            mtv_due_date.setText("Due Until: " + mViewModel.GetFormattedDate(eAnnualMaster.getDDueDatex(), "MMMM d, yyyy"));

                            mViewModel.GetAnnualDetail(eAnnualMaster.getSTransNox()).observe(getViewLifecycleOwner(), new Observer<List<AnnualMembers>>() {
                                @Override
                                public void onChanged(List<AnnualMembers> annualMembers) {

                                    if (annualMembers == null || annualMembers.size() < 1){
                                        layout_no_record.setVisibility(View.VISIBLE);
                                        layout_ledger.setVisibility(View.GONE);
                                        return;
                                    }
                                    layout_no_record.setVisibility(View.GONE);
                                    layout_ledger.setVisibility(View.VISIBLE);

                                    //adjust view of lists
                                    rcv_fund_list.setPadding(10, 10, 10, 10);
                                    layout_ledger.setUseCompatPadding(true);

                                    double ldbl_totaltrans = 0.00;
                                    double ldbl_totalcoll = 0.00;
                                    for (AnnualMembers loItem : annualMembers){

                                        ldbl_totaltrans += Double.parseDouble(loItem.getNAmtDuexx());
                                        ldbl_totalcoll += Double.parseDouble(loItem.getNAmtPaidx());
                                    }
                                    mtv_trans.setText(String.valueOf(ldbl_totaltrans));
                                    mtv_collect.setText(String.valueOf(ldbl_totalcoll));

                                    Adapter_Annual_Members loAdapter = new Adapter_Annual_Members(annualMembers);

                                    rcv_fund_list.setAdapter(loAdapter);
                                    rcv_fund_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                                }
                            });
                        }
                    });

                }else{

                    if (lodgeCalendarLists.size() > 0) mtv_lodge_name.setText(lodgeCalendarLists.get(0).getSLodgeNme());
                    mtv_lodge_year.setVisibility(View.GONE);

                    mtv_transaction.setText("Transaction Summary");

                    //display summary of annual master from lodge id
                    mViewModel.GetAnnualSummary(lsLodgeIDxx, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EAnnualMaster>>() {
                        @Override
                        public void onChanged(List<EAnnualMaster> eAnnualMasters) {

                            if (eAnnualMasters == null || eAnnualMasters.size() < 1) return;

                            List<AnnualSummary> laSummary = new ArrayList<>(); //list for display
                            List<Date> laDueDates = new ArrayList<>(); //list for getting max date

                            double ldbl_totaltrans = 0.00;
                            double ldbl_totalcoll = 0.00;

                            for (EAnnualMaster loMaster : eAnnualMasters){

                                if (mViewModel.GetAnnualDetailSummary(loMaster.getSTransNox()) == null) continue;

                                laSummary.add(mViewModel.GetAnnualDetailSummary(loMaster.getSTransNox()));
                                laDueDates.add(mViewModel.GetStringDate(loMaster.getDDueDatex(), "yyyy-MM-dd"));

                                ldbl_totaltrans += mViewModel.GetAnnualDetailSummary(loMaster.getSTransNox()).getNTotalTrans();
                                ldbl_totalcoll += mViewModel.GetAnnualDetailSummary(loMaster.getSTransNox()).getNTotalColl();
                            }

                            //display the max date from list
                            Date maxDate = Collections.max(laDueDates);
                            mtv_due_date.setText("Up to " + mViewModel.GetFormatDateString(maxDate, "MMMM d, yyyy"));

                            //sort list descending with highest to lowest due total amount
                            laSummary.sort(Comparator.comparingDouble(AnnualSummary::getNTotalColl).reversed());

                            mtv_trans.setText(String.valueOf(ldbl_totaltrans));
                            mtv_collect.setText(String.valueOf(ldbl_totalcoll));

                            if (laSummary.size() < 1){
                                layout_no_record.setVisibility(View.VISIBLE);
                                layout_ledger.setVisibility(View.GONE);
                                return;
                            }
                            layout_no_record.setVisibility(View.GONE);
                            layout_ledger.setVisibility(View.VISIBLE);

                            //adjust view of lists
                            rcv_fund_list.setPadding(0, 0, 0, 0);
                            layout_ledger.setUseCompatPadding(false);

                            Adapter_Annual_Summary loAdapter = new Adapter_Annual_Summary(laSummary);

                            rcv_fund_list.setAdapter(loAdapter);
                            rcv_fund_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                        }
                    });
                }
            }
        });
    }

    private void InitListener(){

        btn_download.setOnClickListener(view -> {

            //year id is not empty, loads only single year transaction, else, loads range of year transactions
            if (lsYearIDxx.isEmpty()){

                MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                loBuilder.setTitleText("Select Date Range");

                MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {

                        //set date range parameters for downloading history
                        lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                        lsDto = mViewModel.GetFormatLongDate(selection.second);

                        //filter list by date
                        DownloadInfo();
                    }
                });
                loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

            }else {

                DownloadInfo();
            }

        });
    }
}