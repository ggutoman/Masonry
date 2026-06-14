package com.gag.masonry.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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

import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import com.gag.masonry.Adapter.Officer_History_Adapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.OfficerHistory;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Officer_history extends Fragment {

    private VM_Main mViewmodel;
    private LoadDialog poDialog;
    private Message_Dialog poMessage;
    private Officer_History_Adapter loAdapter;

    private String lsQuery;
    private String lsdFrom, lsDto;

    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_officer_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__officer_history, container, false);

        mViewmodel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poDialog = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poDialog.InitDialog();
        poMessage.InitDialog();

        lsdFrom = mViewmodel.GetFirstQuarter();
        lsDto = mViewmodel.GetCurrentDate();

        DownloadList();

        InitViews(view);
        InitListener();
        InitDataReceiver();

        return view;
    }

    private void InitViews(View view){

        tie_search = view.findViewById(R.id.tie_search);
        btn_filter = view.findViewById(R.id.btn_filter);
        rcv_officer_list = view.findViewById(R.id.rcv_officer_list);
    }

    private String GetBaseQuery() {
        return " (a.dTransact BETWEEN '" + lsdFrom + "' AND '" + lsDto + "') ";
    }

    private void InitDataReceiver(){

        lsQuery = GetBaseQuery();

        //if glp id is passed, add to condition to filter only one officer information
        String lsMemberID;
        if (getArguments() == null || getArguments().getString("fsGLPIDxx").isEmpty()){

            mViewmodel.SearchOfficerHistory(lsQuery);
        }else {
            lsMemberID = getArguments().getString("fsGLPIDxx");

            //initialize default filter with member id
            lsQuery += " AND (b.sGLPIDNoX= '" + lsMemberID + "') ";

            mViewmodel.SearchOfficerHistory(lsQuery);
        }

        mViewmodel.FilterHistory().observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(String s) {

                if (s.isEmpty()) return;

                loAdapter = new Officer_History_Adapter(mViewmodel.SearchOfficerHistory(lsQuery + s));

                loAdapter.notifyDataSetChanged();

                rcv_officer_list.setAdapter(loAdapter);
                rcv_officer_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
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

                //if empty search, query with default filter
                if (charSequence.length() < 1){
                    mViewmodel.SearchOfficerHistory(lsQuery);
                }else {
                    mViewmodel.FilterOfficerHistory(" AND (b.sFrstName || ' ' || b.sLastName) LIKE '%" + charSequence.toString() + "%' ");
                }
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_officer_history, loMenu.getMenu());
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
                                    lsdFrom = mViewmodel.GetFormattedDate(selection.first);
                                    lsDto = mViewmodel.GetFormattedDate(selection.second);

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
                                    lsdFrom = mViewmodel.GetFormattedDate(selection.first);
                                    lsDto = mViewmodel.GetFormattedDate(selection.second);

                                    poMessage.ShowMessage(2,  "Do you want download list of member entries within " + lsdFrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
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
                        return false;
                    }
                });
            }
        });
    }

    private void DownloadList(){

        String lsMemberID;
        if (getArguments() == null || getArguments().getString("fsGLPIDxx").isEmpty()){
            lsMemberID = "";
        }else {
            lsMemberID = getArguments().getString("fsGLPIDxx");
        }

        mViewmodel.DownloadOfficerHistory(lsMemberID, lsdFrom, lsDto, new VM_Main.OnDownloadData() {
            @Override
            public void OnDownload() {
                poDialog.ShowDialog("Downloading history. Plaese wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poDialog.DismissDialog();
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
}