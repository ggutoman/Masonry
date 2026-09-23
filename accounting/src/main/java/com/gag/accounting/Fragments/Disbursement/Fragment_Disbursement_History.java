package com.gag.accounting.Fragments.Disbursement;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.accounting.Adapter.Disbursement.Adapter_Disbursement_List;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Disbursement;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Disbursement_History extends Fragment {

    private VM_Disbursement mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private Adapter_Disbursement_List loAdapter;

    private ConstraintLayout layout_no_record;
    private MaterialTextView mtv_title;
    private ImageButton btn_filter;
    private TextInputEditText tie_search;
    private TabLayout tab_layout;
    private ExpandableListView rcv_list;

    private int lnTabSelected = 0;
    private String lsStackIDxx, lsDfrom, lsDto, lsLodgeIDxx;
    private List<String> laStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__disbursement__history, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Disbursement.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoad.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }
        laStatus = lsStackIDxx.equalsIgnoreCase("disbursement_list") ? List.of("0", "1", "2", "3", "4") : List.of("2");

        lsDfrom = mViewModel.GetCountedDate(1, 0, false);
        lsDto = mViewModel.GetCurrentDate();

        lsLodgeIDxx = getArguments() == null || getArguments().getString("lodge_id") == null ? "" : getArguments().getString("lodge_id");

        InitViews(view);
        InitListener();
        InitDataReceiver();

        DownloadList();

        mtv_title.setText(lsStackIDxx.equalsIgnoreCase("disbursement_list") ? "List of Payments" : "Released Payments");

        return view;
    }

    private void InitViews(View view){

        mtv_title = view.findViewById(R.id.mtv_title);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        btn_filter = view.findViewById(R.id.btn_filter);
        tie_search = view.findViewById(R.id.tie_search);
        tab_layout = view.findViewById(R.id.tab_layout);
        rcv_list = view.findViewById(R.id.rcv_list);
    }

    public void InitDataReceiver(){

        mViewModel.GetDisbursementList(lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EDisbursement>>() {
            @Override
            public void onChanged(List<EDisbursement> eDisbursementList) {

                if (eDisbursementList == null){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_list.setVisibility(View.GONE);
                }else {

                    List<String> laParents = new ArrayList<>();
                    HashMap<String, List<EDisbursement>> laDisb = new HashMap<>();

                    for (EDisbursement loMaster : eDisbursementList){

                        //add list based on status initialized
                        if (laStatus.contains(loMaster.getCTranStat())){

                            String lsSource= loMaster.getSPayeeNme().toUpperCase();
                            if (lnTabSelected > 0){

                                if (loMaster.getSSourceCD().equalsIgnoreCase("PROJ")){
                                    lsSource = mViewModel.GetProjectInfo(loMaster.getSSourceNo()) == null ? loMaster.getSSourceNo() : mViewModel.GetProjectInfo(loMaster.getSSourceNo()).getSProjctNm();
                                }else{
                                    lsSource = mViewModel.GetLodgeInfo(loMaster.getSSourceNo()) == null ? loMaster.getSSourceNo() : mViewModel.GetLodgeInfo(loMaster.getSSourceNo()).getSLodgeNme();
                                }
                            }

                            //add as group parent if not yet added
                            if (!laParents.contains(lsSource)) laParents.add(lsSource);

                            //initialize new map list if not yet added
                            if (!laDisb.containsKey(lsSource)) laDisb.put(lsSource, new ArrayList<>());
                            laDisb.get(lsSource).add(loMaster);
                        }
                    }

                    if (laDisb.size() < 1){

                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_list.setVisibility(View.GONE);
                        return;
                    }
                    layout_no_record.setVisibility(View.GONE);
                    rcv_list.setVisibility(View.VISIBLE);

                    loAdapter = new Adapter_Disbursement_List(requireActivity(), laParents, laDisb, new Adapter_Disbursement_List.OnSelectItem() {
                        @Override
                        public void OnSelec(EDisbursement loItem) {

                            FragmentTransaction loTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                            Fragment loFragment = new Fragment();
                            Bundle loBundle = new Bundle();

                            switch (lsStackIDxx){

                                case "disbursement_list":

                                    loFragment = new Fragment_Disbursement();

                                    loBundle.putString("transaction_id", loItem.getSTransNox());
                                    loBundle.putString("lodge_id", lsLodgeIDxx);
                                    loFragment.setArguments(loBundle);
                                    break;

                                case "disbursement_confirm":

                                    loFragment = new Fragment_Disbursement_Detail();

                                    loBundle.putString("transaction_id", loItem.getSTransNox());
                                    loBundle.putString("lodge_id", lsLodgeIDxx);
                                    loFragment.setArguments(loBundle);
                                    break;
                            }

                            loTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment)
                                    .addToBackStack(lsStackIDxx);
                            loTransaction.commit();

                        }
                    });
                    rcv_list.setAdapter(loAdapter);
                }
            }
        });
    }

    private void DownloadList(){

        mViewModel.DownloadDisbursementList(lsDfrom, lsDto, laStatus, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading disbursements. Please wait . . ");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();

                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                InitDataReceiver();
            }
        });
    }

    private void InitListener(){

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_disbursement, loMenu.getMenu());

                //hide category filtering if tab is for unconfirmed status only
                loMenu.getMenu().findItem (R.id.action_by_category).setVisible(lsStackIDxx.equalsIgnoreCase("disbursement_list"));
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
                                    lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                                    lsDto = mViewModel.GetFormatLongDate(selection.second);

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
                                    lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                                    lsDto = mViewModel.GetFormatLongDate(selection.second);

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

                        }else if (menuItem.getItemId() == R.id.action_item_all){

                            if (loAdapter == null) return true;

                            laStatus = List.of("0", "1", "2", "3", "4", "5");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_for_open){

                            if (loAdapter == null) return true;

                            laStatus = List.of("0");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_confirmed){

                            if (loAdapter == null) return true;

                            laStatus = List.of("1");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_release){

                            if (loAdapter == null) return true;

                            laStatus = List.of("2");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_cancelled){

                            if (loAdapter == null) return true;

                            laStatus = List.of("3");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_void){

                            if (loAdapter == null) return true;

                            laStatus = List.of("4");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_completed){

                            if (loAdapter == null) return true;

                            laStatus = List.of("5");
                            loAdapter.InitStatus(laStatus);
                            loAdapter.Filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }
                        return false;
                    }
                });

            }
        });

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lnTabSelected = tab.getPosition();
                rcv_list.setAdapter((ExpandableListAdapter) null);

                InitDataReceiver();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null) return;
                loAdapter.Filter(charSequence.toString());
            }
        });
    }
}