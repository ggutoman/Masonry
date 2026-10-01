package com.gag.masonry.Fragment;

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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gag.accounting.Fragments.Receipt.Fragment_Receipt;
import com.gag.masonry.Adapter.Adapter_Officer_List;
import com.gag.masonry.R;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.gag.useraccount.ViewModel.VM_Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Member_List;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EUserObjects;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;

public class Fragment_Member_List extends Fragment {

    private VM_Member mviewModel;
    private Adapter_Member_List loMemberAdaoter;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsStackIDxx, lsGLPIDxx, lsDfrom, lsDto, lsYear;

    private Adapter_Officer_List loOfficerAdapter;

    private TabLayout tab_layout;
    private ConstraintLayout layout_no_record;
    private MaterialTextView mtv_no_record, mtv_lodge, mtv_year;
    private TextInputLayout til_search;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_members;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment__member__list, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Member.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        lsGLPIDxx = getArguments() == null ? "" : getArguments().getString("user_id");
        lsYear = mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy");

        //get max date for december
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(lsYear));
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //initialize default range of date to display for the whole year
        lsDfrom = mviewModel.GetFormattedDate(lsYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mviewModel.GetFormattedDate(lsYear + "-12-" + String.valueOf(maxDay), "yyyy-MM-dd");

        poLoad.InitDialog();
        poMessage.InitDialog();

        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackIDxx = entry.getName() == null ? "" : entry.getName();
        }

        InitViews(view);
        InitDataReceiver();
        InitListener();

        mtv_year.setText("(M.Y " + lsYear + " - " + String.valueOf(Integer.parseInt(lsYear) + 1) + ")");

        DownloadOfficerList();

        return view;
    }

    private void InitViews(View view){

        mtv_lodge = view.findViewById(R.id.mtv_lodge);
        tab_layout = view.findViewById(R.id.tab_layout);
        mtv_year = view.findViewById(R.id.mtv_year);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        mtv_no_record = view.findViewById(R.id.mtv_no_record);
        btn_filter= view.findViewById(R.id.btn_filter);
        til_search = view.findViewById(R.id.til_search);
        tie_search = view.findViewById(R.id.tie_search);
        rcv_members = view.findViewById(R.id.rcv_home);
    }

    private void InitDataReceiver(){

        mviewModel.ObserveMemberInfo(lsGLPIDxx).observe(getViewLifecycleOwner(), new Observer<EMemberInfo>() {
            @Override
            public void onChanged(EMemberInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                mviewModel.GetLodgeCalendar().observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        for (LodgeCalendarList loCalendar : lodgeCalendarLists){

                            if (loCalendar.getNYearxxxx().equalsIgnoreCase(mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy"))){
                                mtv_lodge.setText(loCalendar.getSLodgeNme());
                                break;
                            }
                        }

                        if (tab_layout.getSelectedTabPosition() == 0){

                            mviewModel.GetMemberList(eMemberInfo.getSMemberID(), lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
                                @Override
                                public void onChanged(List<EMemberInfo> eMemberInfos) {

                                    if (eMemberInfos == null || eMemberInfos.size() < 1){
                                        layout_no_record.setVisibility(View.VISIBLE);
                                        rcv_members.setVisibility(View.GONE);

                                        mtv_no_record.setText("No members found on this lodge");
                                        return;
                                    }
                                    layout_no_record.setVisibility(View.GONE);
                                    rcv_members.setVisibility(View.VISIBLE);

                                    loMemberAdaoter = new Adapter_Member_List(requireActivity(), eMemberInfos, new Adapter_Member_List.OnSelect() {
                                        @Override
                                        public void Selected(EMemberInfo foMember) {

                                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                            Fragment loFragment = new Fragment();
                                            Bundle loBundle = new Bundle();

                                            EUserObjects loPermission = mviewModel.GetObjectPermissions(lsStackIDxx.equalsIgnoreCase("view_members") ? "005" : "016");
                                            String lsError = lsStackIDxx.equalsIgnoreCase("view_members") ? "modify member's data" : "create receipts";

                                            if (loPermission == null || !loPermission.getCRecdStat().equalsIgnoreCase("1")) {
                                                Toast.makeText(requireActivity(), "Account is not allowed to " + lsError, Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            switch (lsStackIDxx){

                                                case "view_members":
                                                    loFragment = new Fragment_Member();
                                                    loBundle.putString("fsGLPIDxx", foMember.getSMemberID());

                                                    loFragment.setArguments(loBundle);
                                                    break;

                                                case "receipt_entry":
                                                    loFragment = new Fragment_Receipt();
                                                    loBundle.putString("member_id", foMember.getSMemberID());

                                                    loFragment.setArguments(loBundle);
                                                    break;
                                            }
                                            //add to child container, as bridge to initiate another fragment after call
                                            fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                                            fragmentTransaction.addToBackStack(lsStackIDxx);
                                            fragmentTransaction.commit();
                                        }
                                    });
                                    rcv_members.setAdapter(loMemberAdaoter);
                                    rcv_members.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                                }
                            });

                        }else {

                            mviewModel.ObserveOfficerList(eMemberInfo.getSMemberID(), lsYear).observe(getViewLifecycleOwner(), new Observer<List<OfficerInfo>>() {
                                @Override
                                public void onChanged(List<OfficerInfo> officerInfos) {

                                    if (officerInfos == null || officerInfos.size() < 1){
                                        layout_no_record.setVisibility(View.VISIBLE);
                                        rcv_members.setVisibility(View.GONE);

                                        mtv_no_record.setText("No officers found on this lodge");
                                        return;
                                    }
                                    layout_no_record.setVisibility(View.GONE);
                                    rcv_members.setVisibility(View.VISIBLE);

                                    loOfficerAdapter = new Adapter_Officer_List(requireActivity(), officerInfos, new Adapter_Officer_List.OnSelect() {
                                        @Override
                                        public void Selected(OfficerInfo poItem) {

                                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                            Fragment loFragment = new Fragment();
                                            Bundle loBundle = new Bundle();

                                            EUserObjects loPermission = null;
                                            String lsError = "";

                                            switch (lsStackIDxx){

                                                case "view_members":

                                                    lsError = "modify member's data";
                                                    loPermission = mviewModel.GetObjectPermissions("005");

                                                    loFragment = new Fragment_Assign_Officer();
                                                    loBundle.putString("fsYearID", poItem.getSYearIDxx());
                                                    loBundle.putString("fsMemberID", poItem.getSMemberID());

                                                    loFragment.setArguments(loBundle);
                                                    break;

                                                case "view_officer_history":

                                                    lsError = "view officer's history";
                                                    loPermission = mviewModel.GetObjectPermissions("008");

                                                    loFragment = new Fragment_Officer_history();
                                                    loBundle.putString("member_id", poItem.getSMemberID());

                                                    loFragment.setArguments(loBundle);
                                                    break;

                                                case "receipt_entry":

                                                    lsError = "create receipts";
                                                    loPermission = mviewModel.GetObjectPermissions("016");

                                                    loFragment = new Fragment_Receipt();
                                                    loBundle.putString("member_id", poItem.getSMemberID());

                                                    loFragment.setArguments(loBundle);
                                                    break;
                                            }

                                            if (loPermission == null || !loPermission.getCRecdStat().equalsIgnoreCase("1")) {
                                                Toast.makeText(requireActivity(), "Account is not allowed to " + lsError, Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            //add to child container, as bridge to initiate another fragment after call
                                            fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, loFragment);
                                            fragmentTransaction.addToBackStack(lsStackIDxx);
                                            fragmentTransaction.commit();

                                        }
                                    });
                                    rcv_members.setAdapter(loOfficerAdapter);
                                    rcv_members.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                                }
                            });

                        }
                    }
                });
            }
        });
    }

    private void DownloadOfficerList(){

        if (tab_layout.getSelectedTabPosition() == 0){

            mviewModel.DownloadMembers(lsDfrom,lsDto, new VM_Member.OnDownload() {
                @Override
                public void Loading() {
                    poLoad.ShowDialog("Downloading members. Please wait . .");
                }

                @Override
                public void Finished(String fsMessage) {
                    poLoad.DismissDialog();
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                    InitDataReceiver();
                }
            });

        }else {

            mviewModel.DownloadOfficers(lsYear, new VM_Member.OnDownload() {
                @Override
                public void Loading() {
                    poLoad.ShowDialog("Downloading members. Please wait . .");
                }

                @Override
                public void Finished(String fsMessage) {
                    poLoad.DismissDialog();
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                    InitDataReceiver();
                }
            });
        }
    }

    private void ShowDateChooser(boolean isDownload){

        if (tab_layout.getSelectedTabPosition() == 0){

            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
            loBuilder.setTitleText("Select Date Range");

            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
            loPicker.addOnPositiveButtonClickListener(selection -> {

                //set date range parameters for downloading history
                lsDfrom = mviewModel.GetFormattedDate(selection.first);
                lsDto = mviewModel.GetFormattedDate(selection.second);

                //display year filtered
                String lStartYear = mviewModel.GetFormattedDate(lsDfrom, "yyyy");
                String lsEndYear = mviewModel.GetFormattedDate(lsDfrom, "yyyy");

                if (isDownload){

                    poMessage.ShowMessage(2,  "Do you want download list of member entries within " + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();

                            mtv_year.setText("(M.Y " + lStartYear + " - " + String.valueOf(Integer.parseInt(lsEndYear) + 1) + ")");
                            DownloadOfficerList();
                        }
                    });
                    return;
                }
                mtv_year.setText("(M.Y " + lStartYear + " - " + String.valueOf(Integer.parseInt(lsEndYear) + 1) + ")");
                InitDataReceiver();

            });
            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

        }else {

            NumberPicker yearPicker = new NumberPicker(requireContext());
            yearPicker.setMinValue(1600);
            yearPicker.setMaxValue(9999);
            yearPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));

            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(yearPicker);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select Year")
                    .setView(layout)
                    .setPositiveButton("OK", (dialog, which) -> {

                        lsYear = String.valueOf(yearPicker.getValue());
                        if (isDownload){

                            poMessage.ShowMessage(2,  "Do you want download list of officers for the year " + lsYear + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                @Override
                                public void OnPositive(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();
                                }

                                @Override
                                public void OnNegative(@NotNull AlertDialog poDialog) {
                                    poDialog.dismiss();

                                    mtv_year.setText("(M.Y " + lsYear + " - " + String.valueOf(Integer.parseInt(lsYear) + 1) + ")");
                                    DownloadOfficerList();
                                }
                            });
                            return;
                        }
                        mtv_year.setText("(M.Y " + lsYear + " - " + String.valueOf(Integer.parseInt(lsYear) + 1) + ")");
                        InitDataReceiver();

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

    }

    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //filter and load data based on user level
                if (tab_layout.getSelectedTabPosition() == 0){

                    if (loMemberAdaoter == null) return;

                    loMemberAdaoter.GetFilter().filter(charSequence);
                    loMemberAdaoter.notifyDataSetChanged();

                }else {

                    if (loOfficerAdapter == null) return;

                    loOfficerAdapter.GetFilter().filter(charSequence);
                    loOfficerAdapter.notifyDataSetChanged();
                }
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //initialize pop up object based on user level
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_home, loMenu.getMenu());

                if (tab_layout.getSelectedTabPosition() == 0){ //members tab
                    loMenu.getMenu().findItem (R.id.action_by_status).setVisible(true);
                }else {
                    loMenu.getMenu().findItem (R.id.action_by_status).setVisible(false);
                }
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_item_filter){

                           ShowDateChooser(false);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_download){

                            ShowDateChooser(true);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_all){
                            InitFilterStatus(-1);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_regular){
                            InitFilterStatus(0);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_life){
                            InitFilterStatus(1);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_dual){
                            InitFilterStatus(2);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_deceased){
                            InitFilterStatus(3);
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

                //display hints based on user level
                if (tab_layout.getSelectedTabPosition() == 0){
                    til_search.setHint("Search Member");
                }else {
                    til_search.setHint("Search Officer");
                }
                InitDataReceiver();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void InitFilterStatus(int status){

        if (tab_layout.getSelectedTabPosition() == 0){

            if (loMemberAdaoter == null) return;

            List<String> filterStat;
            if (status < 0){
                filterStat = List.of("0", "1", "2", "3", "4");
            }else {
                filterStat = List.of(String.valueOf(status));
            }

            loMemberAdaoter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
            loMemberAdaoter.GetFilter().InitStatus(filterStat);
            loMemberAdaoter.notifyDataSetChanged();

        }else {

            if (loOfficerAdapter == null) return;

            List<String> filterStat;
            if (status < 0){
                filterStat = List.of("0", "1", "2", "3", "4", "5");
            }else {
                filterStat = List.of(String.valueOf(status));
            }

            loOfficerAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
            loOfficerAdapter.GetFilter().InitStatus(filterStat);
            loOfficerAdapter.notifyDataSetChanged();
        }
    }

}