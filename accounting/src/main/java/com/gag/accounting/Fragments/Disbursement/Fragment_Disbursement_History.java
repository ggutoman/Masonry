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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.Room.Entities.EDisbursement;
import org.gag.appdriver.Utilities.Fragment_Attachment_List;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Disbursement_History extends Fragment {

    private VM_Disbursement mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private Adapter_Disbursement_List loAdapter;

    private TabLayout layout_tab;
    private ConstraintLayout layout_no_record;
    private ImageButton btn_filter;
    private TextInputEditText tie_search;
    private RecyclerView rcv_list;

    private int lnTabSelected;
    private String lsStackIDxx, lsDfrom, lsDto;
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

        lnTabSelected = 0;
        laStatus = List.of("0");

        lsDfrom = mViewModel.GetCountedDate(7, 1, false);
        lsDto = mViewModel.GetCurrentDate();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(view);
        InitListener();
        InitDataReceiver();

        DownloadList();

        return view;
    }

    private void InitViews(View view){

        layout_tab = view.findViewById(R.id.layout_tab);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        btn_filter = view.findViewById(R.id.btn_filter);
        tie_search = view.findViewById(R.id.tie_search);
        rcv_list = view.findViewById(R.id.rcv_list);
    }

    public void InitDataReceiver(){

        mViewModel.GetDisbursementList(lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EDisbursement>>() {
            @Override
            public void onChanged(List<EDisbursement> eDisbursementList) {

                List<EDisbursement> filteredList = new ArrayList<>();

                if (eDisbursementList == null){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_list.setVisibility(View.GONE);
                    return;
                }else {

                    for (EDisbursement loMaster : eDisbursementList){

                        //add list based on status initialized
                        if (laStatus.contains(loMaster.getCTranStat())){
                            filteredList.add(loMaster);
                        }
                    }

                    if (filteredList.size() < 1){

                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_list.setVisibility(View.GONE);
                        return;
                    }
                    layout_no_record.setVisibility(View.GONE);
                    rcv_list.setVisibility(View.VISIBLE);
                }

                loAdapter = new Adapter_Disbursement_List(
                        filteredList,
                        new Adapter_Disbursement_List.OnSelectItem() {
                            @Override
                            public void OnSelec(EDisbursement loItem) {

                                FragmentTransaction loTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                                Fragment loFragment;
                                Bundle loBundle = new Bundle();

                                switch (lsStackIDxx){

                                    case "disbursement_list":

                                        loFragment = new Fragment_Disbursement();

                                        loBundle.putString("transaction_id", loItem.getSTransNox());
                                        loFragment.setArguments(loBundle);

                                        loTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("disbursement_list", loFragment))
                                                .addToBackStack("disbursement_list");

                                        break;

                                    case "disbursement_confirm":

                                        loFragment = new Fragment_Disbursement_Detail();

                                        loBundle.putString("transaction_id", loItem.getSTransNox());
                                        loFragment.setArguments(loBundle);

                                        loTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("disbursement_confirm", loFragment))
                                                .addToBackStack("disbursement_confirm");

                                        break;

                                    case "disbursement_attachments":

                                        loFragment = new Fragment_Attachment_List();

                                        loBundle.putString("source", loItem.getSTransNox());
                                        loFragment.setArguments(loBundle);

                                        loTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("disbursement_attachments", loFragment))
                                                .addToBackStack("disbursement_attachments");

                                        break;
                                }
                                loTransaction.commit();

                            }
                        }
                );
                loAdapter.GetFilter().InitStatus(laStatus);

                rcv_list.setAdapter(loAdapter);
                rcv_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
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
                loMenu.getMenu().findItem (R.id.action_by_category).setVisible(lnTabSelected > 0);
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
                            laStatus = List.of("0", "1", "2", "3", "4", "5");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_for_open){
                            laStatus = List.of("0");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_confirmed){
                            laStatus = List.of("1");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_release){
                            laStatus = List.of("2");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_cancelled){
                            laStatus = List.of("3");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_void){
                            laStatus = List.of("4");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_completed){
                            laStatus = List.of("5");
                            loAdapter.GetFilter().InitStatus(laStatus);
                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());

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

        layout_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //display list based on tab selected
                lnTabSelected = layout_tab.getSelectedTabPosition();
                if (lnTabSelected > 0){
                    laStatus = List.of("1", "2", "3", "4", "5");
                }else {
                    laStatus = List.of("0");
                }
                InitDataReceiver();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}