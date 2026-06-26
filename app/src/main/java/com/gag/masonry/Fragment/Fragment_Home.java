package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.ViewModel.VM_Annual;
import com.gag.masonry.Adapter.Adapter_Member_List;
import com.gag.masonry.Adapter.Adapter_Officer_List;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Fragments.Fragment_Assign_Officer;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Annual_Member_Info;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.App.Models.OfficerInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private VM_Annual mAnnualViewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_lodge;
    private TabLayout tab_layout;
    private Adapter_Member_List loMemberAdaoter;
    private Adapter_Officer_List loOfficerAdapter;
    private Adapter_Annual_Member_Info loAnualAdapter;

    private ConstraintLayout layout_no_record, layout_records, layout_no_record_pledge;
    private LinearLayout layout_pledges;
    private MaterialTextView mtv_no_record, btn_download;
    private TextInputLayout til_search;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_home, rcv_pledges;
    private BottomNavigationView bar_menu;

    private int bottomBarSelect = 0;

    private String lsDfrom, lsDto, lsMemberId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        mAnnualViewModel = new ViewModelProvider(requireActivity()).get(VM_Annual.class);
        poMessage = new Message_Dialog(requireActivity());

        poMessage.InitDialog();

        String lsCurrentYear = mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy");

        //initialize default range of date to display for the whole year
        lsDfrom = mviewModel.GetFormattedDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mviewModel.GetFormattedDate(lsCurrentYear + "-12-30", "yyyy-MM-dd");

        InitViews(view);
        InitData();
        InitListeners();

        return view;
    }

    private void InitViews(View view){

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);

        //officer objects
        tab_layout = view.findViewById(R.id.tab_layout);
        layout_records = view.findViewById(R.id.layout_records);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        mtv_no_record = view.findViewById(R.id.mtv_no_record);
        btn_filter= view.findViewById(R.id.btn_filter);
        til_search = view.findViewById(R.id.til_search);
        tie_search = view.findViewById(R.id.tie_search);
        rcv_home = view.findViewById(R.id.rcv_home);

        //member objects
        bar_menu = view.findViewById(R.id.bar_menu);
        btn_download = view.findViewById(R.id.btn_download);
        rcv_pledges = view.findViewById(R.id.rcv_pledges);
        layout_pledges = view.findViewById(R.id.layout_pledges);
        layout_no_record_pledge = view.findViewById(R.id.layout_no_record_pledge);
    }

    private void InitData(){

        mviewModel.ObserveMemberInfo().observe(getViewLifecycleOwner(), new Observer<MemberDashboardInfo>() {
            @Override
            public void onChanged(MemberDashboardInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                String firstName  = eMemberInfo.getSFrstName() == null ? "" : eMemberInfo.getSFrstName();
                String middleName = eMemberInfo.getSMiddName() == null ? "" : eMemberInfo.getSMiddName();
                String lastName   = eMemberInfo.getSLastName() == null ? "" : eMemberInfo.getSLastName();
                String suffix     = eMemberInfo.getSSuffixNm()  == null ? "" : eMemberInfo.getSSuffixNm();

                String middleInit = !middleName.isEmpty() ? middleName.substring(0, 1) + "." : "";

                String suffixInit = !suffix.isEmpty() ? " " + suffix : "";

                String fullName = firstName + " " + middleInit + " " + lastName + suffixInit;


                lsMemberId = eMemberInfo.getSMemberID();

                mtv_username.setText(fullName);
                mtv_lodge.setText(eMemberInfo.getSLodgeNme());

                //display the following based on user level, USER (FUND ENTRIES & ANNUAL DUES), ADMIN/OWNER(MEMBERS & OFFICERS LIST )
                if (getArguments() == null || getArguments().getInt("user_level") < 1){

                    //show officer objects for member
                    layout_records.setVisibility(View.GONE);

                    layout_pledges.setVisibility(View.VISIBLE);
                    rcv_pledges.setVisibility(View.VISIBLE);

                    InitUserList();

                }else {

                    //hide officer objects for member
                    layout_records.setVisibility(View.VISIBLE);
                    layout_pledges.setVisibility(View.GONE);

                    InitOfficerList();
                }
            }
        });
    }

    private void InitOfficerList(){

        if (tab_layout.getSelectedTabPosition() == 0){

            mviewModel.GetMemberList(lsMemberId, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
                @Override
                public void onChanged(List<EMemberInfo> eMemberInfos) {

                    if (eMemberInfos == null || eMemberInfos.size() < 1){
                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_home.setVisibility(View.GONE);

                        mtv_no_record.setText("No members found on this lodge");
                        return;
                    }
                    layout_no_record.setVisibility(View.GONE);
                    rcv_home.setVisibility(View.VISIBLE);

                    loMemberAdaoter = new Adapter_Member_List(requireActivity(), eMemberInfos, new Adapter_Member_List.OnSelect() {
                        @Override
                        public void Selected(EMemberInfo foMember) {

                            //do not allow viewing and editing of member details for users with acc level (1)
                            if (getArguments() == null || getArguments().getInt("user_level") < 2) return;

                            Fragment loDetail = new Fragment_Member();

                            Bundle loArgs = new Bundle();
                            loArgs.putString("fsGLPIDxx", foMember.getSGLPIDNoX());

                            loDetail.setArguments(loArgs);

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.layout_container, loDetail)
                                    .addToBackStack("create_member")
                                    .commit();
                        }
                    });

                    rcv_home.setAdapter(loMemberAdaoter);
                    rcv_home.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                }
            });
        }else {

            mviewModel.ObserveOfficerList(lsMemberId, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<OfficerInfo>>() {
                @Override
                public void onChanged(List<OfficerInfo> officerInfos) {

                    if (officerInfos == null || officerInfos.size() < 1){
                        layout_no_record.setVisibility(View.VISIBLE);
                        rcv_home.setVisibility(View.GONE);

                        mtv_no_record.setText("No officers found on this lodge");
                        return;
                    }
                    layout_no_record.setVisibility(View.GONE);
                    rcv_home.setVisibility(View.VISIBLE);

                    loOfficerAdapter = new Adapter_Officer_List(requireActivity(), officerInfos, new Adapter_Officer_List.OnSelect() {
                        @Override
                        public void Selected(OfficerInfo poItem) {

                            //do not allow viewing and editing of officer details for users with acc level (1)
                            if (getArguments() == null || getArguments().getInt("user_level") < 2) return;

                            Fragment loDetail = new Fragment_Assign_Officer();

                            Bundle loArgs = new Bundle();
                            loArgs.putString("fsYearID", poItem.getSYearIDxx());
                            loArgs.putString("fsMemberID", poItem.getSMemberID());

                            loDetail.setArguments(loArgs);

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.layout_container, loDetail)
                                    .addToBackStack("assign_officer")
                                    .commit();
                        }
                    });

                    rcv_home.setAdapter(loOfficerAdapter);
                    rcv_home.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                }
            });

        }
    }

    private void DownloadOfficerList(){

        if (tab_layout.getSelectedTabPosition() == 0){

            mviewModel.DownloadMembers(lsDfrom, lsDto, new VM_Main.OnDownloadData() {
                @Override
                public void OnDownload() { Toast.makeText(requireActivity(), "Downloading members . . .", Toast.LENGTH_SHORT).show(); }

                @Override
                public void OnFinished(String fsMessage) {
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    InitOfficerList();
                }
            });
        }else {

            mviewModel.DownloadOfficers(lsDfrom, lsDto, new VM_Main.OnDownloadData() {
                @Override
                public void OnDownload() { Toast.makeText(requireActivity(), "Downloading officers . . .", Toast.LENGTH_SHORT).show(); }

                @Override
                public void OnFinished(String fsMessage) {
                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    InitOfficerList();
                }
            });
        }
    }

    private void InitUserList(){

        mAnnualViewModel.GetAnnualMemberInfo().observe(getViewLifecycleOwner(), new Observer<List<AnnualSummary>>() {
            @Override
            public void onChanged(List<AnnualSummary> annualMembers) {

                if (annualMembers == null || annualMembers.size() < 1){

                    layout_no_record_pledge.setVisibility(View.VISIBLE);
                    rcv_pledges.setVisibility(View.GONE);

                    return;
                }
                layout_no_record_pledge.setVisibility(View.GONE);
                rcv_pledges.setVisibility(View.VISIBLE);

                loAnualAdapter = new Adapter_Annual_Member_Info(annualMembers);
                rcv_pledges.setAdapter(loAnualAdapter);
                rcv_pledges.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    private void DownloadMemberDues(){

        mAnnualViewModel.DownloadAnnualMembers(new VM_Annual.OnTransaction() {
            @Override
            public void OnLoad() {
                Toast.makeText(requireActivity(), "Downloading Annual Dues . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSuccess() {
                InitUserList();
            }

            @Override
            public void OnFailed(String fsMessage) {
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();

                InitUserList();
            }
        });
    }

    private void InitListeners(){

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

                    loMenu.getMenu().findItem (R.id.action_item_inactive).setVisible(true);

                    //disable other menus
                    loMenu.getMenu().findItem(R.id.action_item_reassign).setVisible(false);
                    loMenu.getMenu().findItem(R.id.action_item_remove).setVisible(false);
                    loMenu.getMenu().findItem(R.id.action_item_resign).setVisible(false);
                    loMenu.getMenu().findItem(R.id.action_item_decease).setVisible(false);
                }else {

                    loMenu.getMenu().findItem (R.id.action_item_inactive).setVisible(false);

                    //disable other menus
                    loMenu.getMenu().findItem(R.id.action_item_reassign).setVisible(true);
                    loMenu.getMenu().findItem(R.id.action_item_remove).setVisible(true);
                    loMenu.getMenu().findItem(R.id.action_item_resign).setVisible(true);
                    loMenu.getMenu().findItem(R.id.action_item_decease).setVisible(true);
                }
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
                                    lsDfrom = mviewModel.GetFormattedDate(selection.first);
                                    lsDto = mviewModel.GetFormattedDate(selection.second);

                                    //initialize list based on user level
                                    InitOfficerList();
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
                                    lsDfrom = mviewModel.GetFormattedDate(selection.first);
                                    lsDto = mviewModel.GetFormattedDate(selection.second);

                                    //initialize message based on user level
                                    String lsMessage = "Do you want download list of member entries within ";
                                    if (tab_layout.getSelectedTabPosition() > 1) lsMessage = "Do you want download list of officer entries within ";

                                    poMessage.ShowMessage(2,  lsMessage + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();

                                            //download list based on user level
                                            DownloadOfficerList();
                                        }
                                    });
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_all){
                            InitFilterStatus(-1);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_inactive){
                            InitFilterStatus(0);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_active){
                            InitFilterStatus(1);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_suspended){
                            InitFilterStatus(2);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_reassign){
                            InitFilterStatus(2);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_remove){
                            InitFilterStatus(3);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_resign){
                            InitFilterStatus(4);
                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_decease){
                            InitFilterStatus(5);
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
                InitOfficerList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        bar_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == com.gag.accounting.R.id.action_item_dues) {
                    bottomBarSelect = 0;
                    return true;
                }else if (item.getItemId() == com.gag.accounting.R.id.action_item_project) {
                    bottomBarSelect = 1;
                    return true;
                }else if (item.getItemId() == com.gag.accounting.R.id.action_item_pledges) {
                    bottomBarSelect = 2;
                    return true;
                }

                return false;
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bottomBarSelect == 0) DownloadMemberDues();
            }
        });
    }

    private void InitFilterStatus(int status){

        if (tab_layout.getSelectedTabPosition() == 0){

            if (loMemberAdaoter == null) return;

            List<String> filterStat;
            if (status < 0){
                filterStat = List.of("0", "1", "2");
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