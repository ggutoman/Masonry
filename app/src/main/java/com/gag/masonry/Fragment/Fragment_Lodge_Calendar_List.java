package com.gag.masonry.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import com.gag.accounting.Fragments.Annual.Fragment_Annual_Due;
import com.gag.accounting.Fragments.Fund.Fragment_Fund_History;
import com.gag.masonry.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.gag.appdriver.App.Adapters.LodgeCalendarListAdapter;
import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.ViewModels.VM_Lodge;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Fragment_Lodge_Calendar_List extends Fragment {

    private VM_Lodge mviewModel;
    private LodgeCalendarListAdapter loAdapter;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;

    private String lsdFrom, lsdTo;

    private ConstraintLayout layout_no_record;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_fund_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lodge_calendar_list, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Lodge.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoad.InitDialog();

        lsdFrom = mviewModel.GetFirstQuarter();
        lsdTo = mviewModel.GetCurrentDate();

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

    @SuppressLint("CommitTransaction")
    private void InitDataReceiver(){

        if (getArguments() == null) return;

        mviewModel.GetLodgeCalendarList(getArguments().getString("lodge_id"), lsdFrom, lsdTo).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
            @Override
            public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null || lodgeCalendarLists.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_fund_list.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_fund_list.setVisibility(View.VISIBLE);

                loAdapter = new LodgeCalendarListAdapter(requireActivity(), lodgeCalendarLists, new LodgeCalendarListAdapter.OnSelectCalendar() {
                    @Override
                    public void Selected(LodgeCalendarList poItem) {

                        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
                        if (count > 0) {

                            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
                            String name = entry.getName() == null ? "" : entry.getName();

                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            Bundle loBundle = new Bundle();

                            switch (name){

                                //view lodge calendar info
                                case "lodge_calendars":

                                    Fragment_Lodge_Calendar_Entry loDetail = new Fragment_Lodge_Calendar_Entry();


                                    loBundle.putString("year_id", poItem.getSYearIDxx());

                                    loDetail.setArguments(loBundle);

                                    fragmentTransaction.replace(R.id.layout_container, loDetail);
                                    fragmentTransaction.addToBackStack("lodge_calendar_list");
                                    break;

                                //open turnover fund history
                                case "lodge_funds":

                                    Fragment_Fund_History loFund = new Fragment_Fund_History();

                                    loBundle = new Bundle();
                                    loBundle.putString("year_id", poItem.getSYearIDxx());

                                    loFund.setArguments(loBundle);

                                    //add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("fund_history", loFund));
                                    fragmentTransaction.addToBackStack("fund_history");
                                    break;
                                //open lodge calendar annual dues
                                case "lodge_annual_dues_info":

                                    Fragment_Annual_Due loAnnual = new Fragment_Annual_Due();

                                    loBundle = new Bundle();
                                    loBundle.putString("lodge_id", poItem.getSLodgeIDx());
                                    loBundle.putString("year_id", poItem.getSYearIDxx());

                                    loAnnual.setArguments(loBundle);

                                    //add to child container, as bridge to initiate another fragment after call
                                    fragmentTransaction.replace(R.id.layout_container, new Fragment_Child_Container().newInstance("annual_due_info", loAnnual));
                                    fragmentTransaction.addToBackStack("annual_due_info");
                                    break;
                            }
                            fragmentTransaction.commit();
                        }

                    }
                });

                rcv_fund_list.setAdapter(loAdapter);
                rcv_fund_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    private void DownloadList(){

        mviewModel.DownloadLodgeCalendars(lsdFrom, lsdTo, new VM_Lodge.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading lodge calendars. Please wait . .");
            }

            @Override
            public void OnSuccess() {
                poLoad.DismissDialog();

                InitDataReceiver();
                Toast.makeText(requireActivity(), "Lodge calendars downloaded successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnError(String fsMessage) {
                poLoad.DismissDialog();

                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });
    }

    private void InitListener(){

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_lodge_calendar, loMenu.getMenu());
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
                                    lsdFrom = mviewModel.GetFormattedDate(selection.first);
                                    lsdTo = mviewModel.GetFormattedDate(selection.second);

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
                                    lsdFrom = mviewModel.GetFormattedDate(selection.first);
                                    lsdTo = mviewModel.GetFormattedDate(selection.second);

                                    poMessage.ShowMessage(2,  "Do you want download list of lodge calendars within " + lsdFrom + " to " + lsdTo + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
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
    }
}