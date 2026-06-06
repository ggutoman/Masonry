package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import com.gag.masonry.Adapter.Adapter_Member_List;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.gag.useraccount.Fragments.Fragment_Member;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.Room.DataObject.DMemberInfo;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_position, mtv_lodge;
    private TabLayout tab_layout;
    private Adapter_Member_List loAdapter;

    private ConstraintLayout layout_no_record;
    private TextInputEditText tie_search;
    private ImageButton btn_filter;
    private RecyclerView rcv_members;

    private String lsDfrom, lsDto, lsMemberId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());

        poMessage.InitDialog();

        lsDfrom = mviewModel.GetFirstQuarter();
        lsDto = mviewModel.GetCurrentDate();

        InitViews(view);
        InitData();
        InitListeners();

        return view;
    }

    private void InitData(){

        mviewModel.GetMemberInfo().observe(getViewLifecycleOwner(), new Observer<DMemberInfo.MemberDashboardInfo>() {
            @Override
            public void onChanged(DMemberInfo.MemberDashboardInfo eMemberInfo) {

                if (eMemberInfo == null) return;

                lsMemberId = eMemberInfo.getSMemberID();

                mtv_username.setText(eMemberInfo.getSMemberNm());
                mtv_lodge.setText(eMemberInfo.getSLodgeNme());

                if (tab_layout.getSelectedTabPosition() == 0){
                    InitMemberList();
                }
            }
        });
    }

    private void InitViews(View view){

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_position = view.findViewById(R.id.mtv_position);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);

        tab_layout = view.findViewById(R.id.tab_layout);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        btn_filter= view.findViewById(R.id.btn_filter);
        tie_search = view.findViewById(R.id.tie_search);
        rcv_members = view.findViewById(R.id.rcv_members);
    }

    private void InitListeners(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null) return;

                loAdapter.GetFilter().filter(charSequence);
                loAdapter.notifyDataSetChanged();
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(requireContext(), view);
                loMenu.getMenuInflater().inflate(R.menu.menu_filter_members, loMenu.getMenu());
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

                                    //retrieve imported data only
                                    InitMemberList();
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

                                    poMessage.ShowMessage(2, "Do you want download list of member entries withtin " + lsDfrom + " to " + lsDto + " ?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                                        @Override
                                        public void OnPositive(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();
                                        }

                                        @Override
                                        public void OnNegative(@NotNull AlertDialog poDialog) {
                                            poDialog.dismiss();

                                            //download data from server with date to get
                                            DownloadMemberList();
                                            InitMemberList();
                                        }
                                    });
                                }
                            });
                            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

                            return true;

                        }else if (menuItem.getItemId() == R.id.action_item_all){

                            if (loAdapter == null) return false;

                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
                            loAdapter.GetFilter().InitStatus(List.of("0", "1", "2"));

                            loAdapter.notifyDataSetChanged();

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_inactive){

                            if (loAdapter == null) return false;

                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
                            loAdapter.GetFilter().InitStatus(List.of("0"));

                            loAdapter.notifyDataSetChanged();

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_active){

                            if (loAdapter == null) return false;

                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
                            loAdapter.GetFilter().InitStatus(List.of("1"));

                            loAdapter.notifyDataSetChanged();

                            return true;
                        }else if (menuItem.getItemId() == R.id.action_item_suspended){

                            if (loAdapter == null) return false;

                            loAdapter.GetFilter().filter(tie_search.getText() == null ? "" : tie_search.getText().toString());
                            loAdapter.GetFilter().InitStatus(List.of("2"));

                            loAdapter.notifyDataSetChanged();

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

                if (tab.getPosition() == 0){
                    InitMemberList(); //initialize list
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void InitMemberList(){

        mviewModel.GetMemberList(lsMemberId, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EMemberInfo>>() {
            @Override
            public void onChanged(List<EMemberInfo> eMemberInfos) {

                if (eMemberInfos == null || eMemberInfos.size() < 1){
                    layout_no_record.setVisibility(View.VISIBLE);
                    rcv_members.setVisibility(View.GONE);
                    return;
                }
                layout_no_record.setVisibility(View.GONE);
                rcv_members.setVisibility(View.VISIBLE);

                loAdapter = new Adapter_Member_List(requireActivity(), eMemberInfos, new Adapter_Member_List.OnSelect() {
                    @Override
                    public void Selected(EMemberInfo foMember) {

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

                rcv_members.setAdapter(loAdapter);
                rcv_members.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

            }
        });
    }

    private void DownloadMemberList(){

        mviewModel.DownloadMembers(lsDfrom, lsDto, new VM_Main.OnDownloadData() {
            @Override
            public void OnDownload() { Toast.makeText(requireActivity(), "Downloading members . . .", Toast.LENGTH_SHORT).show(); }

            @Override
            public void OnFinished(String fsMessage) {
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                InitMemberList();
            }
        });

    }
}