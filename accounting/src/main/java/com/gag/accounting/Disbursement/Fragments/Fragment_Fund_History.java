package com.gag.accounting.Disbursement.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.accounting.Disbursement.Adapter.Adapter_Fund_List;
import com.gag.accounting.Disbursement.ViewModel.VM_Funds;
import com.gag.accounting.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.Room.Entities.EFundTurnOver;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Fund_History extends Fragment {

    private VM_Funds mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private Adapter_Fund_List loAdapter;

    private String lsDfrom, lsDto;

    private ConstraintLayout layout_no_record;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_fund_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_fund_history, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Funds.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoad.InitDialog();

        lsDfrom = mViewModel.GetCurrentDate();
        lsDto = mViewModel.GetCurrentDate();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        layout_no_record = view.findViewById(R.id.layout_no_record);
        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        rcv_fund_list = view.findViewById(R.id.rcv_fund_list);
    }

    private void InitDataReceiver(){

        //require year id to load transaction history
        if (getArguments() == null || getArguments().getString("year_id") == null || getArguments().getString("year_id").isEmpty()){
            Toast.makeText(requireActivity(), "Transaction id is not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.ObserveFundTurnoverList(getArguments().getString("year_id"), lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EFundTurnOver>>() {
            @Override
            public void onChanged(List<EFundTurnOver> eFundTurnOvers) {

                if (eFundTurnOvers == null){
                    rcv_fund_list.setVisibility(View.GONE);
                    layout_no_record.setVisibility(View.VISIBLE);
                    return;
                }

                loAdapter = new Adapter_Fund_List(requireActivity(), eFundTurnOvers);

                rcv_fund_list.setAdapter(loAdapter);
                rcv_fund_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                rcv_fund_list.setVisibility(View.VISIBLE);
                layout_no_record.setVisibility(View.GONE);
            }
        });
    }

    private void DownloadList(){

        if (getArguments() == null) return;

        mViewModel.DownloadFunds(getArguments().getString("year_id"), lsDfrom, lsDto, new VM_Funds.OnSubmit() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading funds. Please wait . . .");
            }

            @Override
            public void OnSucces() {
                poLoad.DismissDialog();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.DismissDialog();

                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null) return;

                loAdapter.GetFilter().filter(charSequence.toString());
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_funds, loMenu.getMenu());
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_filter){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.GetFormattedDate(selection.first);
                                    lsDto = mViewModel.GetFormattedDate(selection.second);

                                    //filter list by date
                                    InitDataReceiver();
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_download){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.GetFormattedDate(selection.first);
                                    lsDto = mViewModel.GetFormattedDate(selection.second);

                                    poMessage.ShowMessage(2,  "Do you want download list of fund entries within " + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();

                                            //download data from server with date to get
                                            DownloadList();
                                        }
                                    });
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }

//                        else if (menuItem.getItemId() == R.id.action_item_all){
//                            InitFilterStatus(-1);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_inactive){
//                            InitFilterStatus(0);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_active){
//                            InitFilterStatus(1);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_suspended){
//                            InitFilterStatus(2);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_reassign){
//                            InitFilterStatus(2);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_remove){
//                            InitFilterStatus(3);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_resign){
//                            InitFilterStatus(4);
//                            return true;
//                        }else if (menuItem.getItemId() == R.id.action_item_decease){
//                            InitFilterStatus(5);
//                            return true;
//                        }
                        return false;
                    }
                });

            }
        });
    }
}