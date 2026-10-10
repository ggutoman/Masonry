package com.gag.accounting.Fragments.Receipt;

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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.accounting.Adapter.Receipt.Adapter_Issued_Receipts;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Receipts;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Models.ReceiptMasterList;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Room.Entities.EUserObjects;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Issued_Receipts extends Fragment {

    private VM_Receipts mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private Adapter_Issued_Receipts loAdapter;

    private ConstraintLayout layout_no_record;
    private MaterialTextView mtv_title;
    private ImageButton btn_filter;
    private TextInputEditText tie_search;
    private TabLayout tab_layout;
    private ExpandableListView rcv_list;

    private int lnTabSelected = 0;
    private String lsStackIDxx, lsDfrom, lsDto;
    private List<String> laStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__issued__receipts, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Receipts.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        poMessage.InitDialog();
        poLoad.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        laStatus = lsStackIDxx.equalsIgnoreCase("issued_receipt") ? List.of("0", "1", "3", "4") : List.of("1");

        lsDfrom = mViewModel.GetCountedDate(1, 0, false);
        lsDto = mViewModel.GetCurrentDate();

        InitViews(view);
        InitListener();
        InitDataReceiver();

        DownloadList();

        //change screen title to identify which are type of lists are displayed
        mtv_title.setText(lsStackIDxx.equalsIgnoreCase("issued_receipt") ? "List of Receipts" : "Issued Receipts");

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

        if (lnTabSelected > 0){

            mViewModel.GetReceiptSources("PROJ", lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<ReceiptMasterList>>() {
                @Override
                public void onChanged(List<ReceiptMasterList> receiptMasterLists) {

                    if (receiptMasterLists == null){
                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_list.setVisibility(View.GONE);
                    }else {

                        List<String> laParents = new ArrayList<>();
                        HashMap<String, List<ReceiptMasterList>> laReceipts = new HashMap<>();
                        for (ReceiptMasterList loMaster : receiptMasterLists){

                            //add list based on status initialized
                            if (laStatus.contains(loMaster.getCTranStat())){

                                //get source description
                                EProjectMaster loProj = mViewModel.GetProjectInfo(loMaster.getSSourceNo());
                                String lsSourceDescr;
                                if (loProj == null){
                                    lsSourceDescr = loMaster.getSSourceNo();
                                }else {
                                    lsSourceDescr = loProj.getSProjctNm();
                                }

                                //add as group parent if not yet added
                                if (!laParents.contains(lsSourceDescr)) laParents.add(lsSourceDescr);

                                //initialize new map list if not yet added
                                if (!laReceipts.containsKey(lsSourceDescr)) laReceipts.put(lsSourceDescr, new ArrayList<>());
                                laReceipts.get(lsSourceDescr).add(loMaster);
                            }
                        }

                        if (laReceipts.size() < 1){

                            layout_no_record.setVisibility(View.VISIBLE);
                            rcv_list.setVisibility(View.GONE);
                            return;
                        }

                        loAdapter = new Adapter_Issued_Receipts(requireActivity(), laParents, laReceipts, new Adapter_Issued_Receipts.OnSelectItem() {
                            @Override
                            public void OnSelect(ReceiptMasterList loItem) {

                                FragmentTransaction loTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                Fragment loFragment = new Fragment();
                                Bundle loBundle = new Bundle();

                                EUserObjects loPermission = mViewModel.GetPermissions(lsStackIDxx.equalsIgnoreCase("issued_receipt") ? "016" : "017");
                                if (loPermission == null || !loPermission.getCRecdStat().equalsIgnoreCase("1")){
                                    Toast.makeText(requireActivity(), "Account is not allowed to modify collections", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                switch (lsStackIDxx){

                                    case "issued_receipt":

                                        loFragment = new Fragment_Receipt();
                                        loBundle.putString("member_id", loItem.getSMemberID());
                                        loBundle.putString("transaction_id", loItem.getSTransNox());

                                        loFragment.setArguments(loBundle);
                                        break;

                                    case "receipt_confirm":

                                        loFragment = new Fragment_Receipt_Detail();
                                        loBundle.putString("member_id", loItem.getSMemberID());
                                        loBundle.putString("transaction_id", loItem.getSTransNox());

                                        loFragment.setArguments(loBundle);
                                        break;
                                }
                                loTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                                loTransaction.addToBackStack(lsStackIDxx);
                                loTransaction.commit();

                            }
                        });

                        rcv_list.setAdapter(loAdapter);
                        layout_no_record.setVisibility(View.GONE);
                        rcv_list.setVisibility(View.VISIBLE);
                    }
                }
            });

        }else {

            mViewModel.GetReceiptMembers(lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<ReceiptMasterList>>() {
                @Override
                public void onChanged(List<ReceiptMasterList> receiptMasterLists) {

                    if (receiptMasterLists == null){
                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_list.setVisibility(View.GONE);
                    }else {

                        List<String> laParents = new ArrayList<>();
                        HashMap<String, List<ReceiptMasterList>> laReceipts = new HashMap<>();
                        for (ReceiptMasterList loMaster : receiptMasterLists){

                            //add list based on status initialized
                            if (laStatus.contains(loMaster.getCTranStat())){

                                //add as group parent if not yet added
                                if (!laParents.contains(loMaster.getSSourceNo())) laParents.add(loMaster.getSSourceNo());

                                //initialize new map list if not yet added
                                if (!laReceipts.containsKey(loMaster.getSSourceNo())) laReceipts.put(loMaster.getSSourceNo(), new ArrayList<>());
                                laReceipts.get(loMaster.getSSourceNo()).add(loMaster);
                            }
                        }

                        if (laReceipts.size() < 1){

                            layout_no_record.setVisibility(View.VISIBLE);
                            rcv_list.setVisibility(View.GONE);
                            return;
                        }

                        loAdapter = new Adapter_Issued_Receipts(requireActivity(), laParents, laReceipts, new Adapter_Issued_Receipts.OnSelectItem() {
                            @Override
                            public void OnSelect(ReceiptMasterList loItem) {

                                FragmentTransaction loTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                Fragment loFragment = new Fragment();
                                Bundle loBundle = new Bundle();

                                EUserObjects loPermission = mViewModel.GetPermissions(lsStackIDxx.equalsIgnoreCase("issued_receipt") ? "016" : "017");
                                if (loPermission == null || !loPermission.getCRecdStat().equalsIgnoreCase("1")){
                                    Toast.makeText(requireActivity(), "Account is not allowed to modify collections", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                switch (lsStackIDxx){

                                    case "issued_receipt":

                                        loFragment = new Fragment_Receipt();
                                        loBundle.putString("member_id", loItem.getSMemberID());
                                        loBundle.putString("transaction_id", loItem.getSTransNox());

                                        loFragment.setArguments(loBundle);
                                        break;

                                    case "receipt_confirm":

                                        loFragment = new Fragment_Receipt_Detail();
                                        loBundle.putString("member_id", loItem.getSMemberID());
                                        loBundle.putString("transaction_id", loItem.getSTransNox());

                                        loFragment.setArguments(loBundle);
                                        break;
                                }
                                loTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                                loTransaction.addToBackStack(lsStackIDxx);
                                loTransaction.commit();

                            }
                        });

                        rcv_list.setAdapter(loAdapter);
                        layout_no_record.setVisibility(View.GONE);
                        rcv_list.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    private void DownloadList(){

        mViewModel.DownloadReceiptList(lsDfrom, lsDto, laStatus, new VM_Receipts.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading receipts. Please wait . . ");
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
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_receipts, loMenu.getMenu());

                //show status filter for viewing history only, not for voiding
                loMenu.getMenu().findItem(R.id.action_by_category).setVisible((lsStackIDxx.equalsIgnoreCase("issued_receipt")));
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

                            laStatus = lsStackIDxx.equalsIgnoreCase("issued_receipt") ? List.of("0", "1", "3", "4") : List.of("1");
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
                loAdapter.Filter(charSequence.toString());
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

    }
}