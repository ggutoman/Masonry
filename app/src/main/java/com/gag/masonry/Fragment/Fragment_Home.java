package com.gag.masonry.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.Adapter.Project.Adapter_Project_Members;
import com.gag.accounting.ViewModel.VM_Annual;
import com.gag.accounting.ViewModel.VM_Projects;
import com.gag.masonry.R;
import com.gag.masonry.ViewModel.VM_Main;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.Adapter_Annual_Member_Info;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.MemberDashboardInfo;
import org.gag.appdriver.App.Models.MemberPledges;
import org.gag.appdriver.Utilities.Message_Dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Home extends Fragment {

    private VM_Main mviewModel;
    private VM_Annual mAnnualViewModel;
    private VM_Projects mProjectsViewModel;
    private Message_Dialog poMessage;

    private MaterialTextView mtv_username, mtv_lodge, mtv_year;

    private Adapter_Annual_Member_Info loAnualAdapter;
    private Adapter_Project_Members loProjAdapter;

    private ConstraintLayout layout_no_record_pledge;
    private MaterialTextView btn_download;
    private RecyclerView rcv_pledges;
    private BottomNavigationView bar_menu;

    private int bottomBarSelect = 0;

    private String lsDfrom, lsDto, lsMemberId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, container, false);

        mviewModel = new ViewModelProvider(requireActivity()).get(VM_Main.class);
        poMessage = new Message_Dialog(requireActivity());

        mAnnualViewModel = new ViewModelProvider(requireActivity()).get(VM_Annual.class);
        mProjectsViewModel = new ViewModelProvider(requireActivity()).get(VM_Projects.class);

        poMessage.InitDialog();

        String lsCurrentYear = mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy");

        //get max date for december
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(lsCurrentYear));
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        //initialize default range of date to display for the whole year
        lsDfrom = mviewModel.GetFormattedDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mviewModel.GetFormattedDate(lsCurrentYear + "-12-" + String.valueOf(maxDay), "yyyy-MM-dd");

        InitViews(view);
        InitData();
        InitListeners();

        return view;
    }

    private void InitViews(View view){

        mtv_username = view.findViewById(R.id.mtv_username);
        mtv_lodge = view.findViewById(R.id.mtv_lodge);
        mtv_year = view.findViewById(R.id.mtv_year);

        bar_menu = view.findViewById(R.id.bar_menu);
        btn_download = view.findViewById(R.id.btn_download);
        rcv_pledges = view.findViewById(R.id.rcv_pledges);
        layout_no_record_pledge = view.findViewById(R.id.layout_no_record_pledge);
    }

    private void InitData(){

        /** DISPLAY HEADER **/

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
            }
        });

        mProjectsViewModel.GetLodgeCalendarList().observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
            @Override
            public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null || lodgeCalendarLists.size() < 1){
                    mtv_year.setVisibility(View.GONE);
                    return;
                }

                for (LodgeCalendarList calendars : lodgeCalendarLists){

                    if (calendars.getNYearxxxx().equalsIgnoreCase(mviewModel.GetFormattedDate(mviewModel.GetCurrentDate(), "yyyy"))){
                        mtv_year.setText("M.Y " + calendars.getNYearxxxx());
                        mtv_year.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

        /** DISPLAY MEMBER DUES && PLEDGER **/

        //first bar, annual dues
        if (bottomBarSelect == 0){

            mAnnualViewModel.GetAnnualMemberInfo().observe(getViewLifecycleOwner(), new Observer<List<AnnualSummary>>() {
                @Override
                public void onChanged(List<AnnualSummary> annualMembers) {

                    if (annualMembers == null || annualMembers.size() < 1){

                        layout_no_record_pledge.setVisibility(View.VISIBLE);
                        rcv_pledges.setVisibility(View.GONE);

                        return;
                    }

                    //display only with amount
                    List<AnnualSummary> laDues = new ArrayList<>();
                    for (AnnualSummary dues : annualMembers){

                        if (dues.getNTotalTrans() > 0.00){
                            laDues.add(dues);
                        }
                    }

                    boolean isShowDues = laDues.size() > 0;
                    layout_no_record_pledge.setVisibility(isShowDues ? View.GONE : View.VISIBLE);
                    rcv_pledges.setVisibility(isShowDues ? View.VISIBLE : View.GONE);

                    loAnualAdapter = new Adapter_Annual_Member_Info(annualMembers);
                    rcv_pledges.setAdapter(loAnualAdapter);
                    rcv_pledges.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                }
            });

        } else if (bottomBarSelect == 1){

            mProjectsViewModel.GetMemberPledges(lsMemberId, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<MemberPledges>>() {
                @Override
                public void onChanged(List<MemberPledges> memberPledges) {

                    if (memberPledges == null || memberPledges.size() < 1){

                        layout_no_record_pledge.setVisibility(View.VISIBLE);
                        rcv_pledges.setVisibility(View.GONE);

                        return;
                    }
                    layout_no_record_pledge.setVisibility(View.GONE);
                    rcv_pledges.setVisibility(View.VISIBLE);

                    loProjAdapter = new Adapter_Project_Members(requireActivity(), memberPledges);
                    rcv_pledges.setAdapter(loProjAdapter);
                    rcv_pledges.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                }
            });
        }
    }

    private void DownloadMemberDues(){

        mAnnualViewModel.DownloadAnnualMembers(new VM_Annual.OnTransaction() {
            @Override
            public void OnLoad() {
                Toast.makeText(requireActivity(), "Downloading Annual Dues . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSuccess() {
                InitData();
            }

            @Override
            public void OnFailed(String fsMessage) {
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                InitData();
            }
        });
    }

    private void DownloadMemberPledge(){

        mProjectsViewModel.DownloadDashboardledges(lsMemberId, new VM_Projects.OnTransaction() {
            @Override
            public void OnLoad() {
                Toast.makeText(requireActivity(), "Downloading Member Pledges . .", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSuccess() {
                InitData();
            }

            @Override
            public void OnFailed(String fsMessage) {
                Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                InitData();
            }
        });
    }

    private void InitListeners(){

        bar_menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //reset display
                layout_no_record_pledge.setVisibility(View.VISIBLE);
                rcv_pledges.setVisibility(View.GONE);

                if (item.getItemId() == com.gag.accounting.R.id.action_item_dues) {
                    bottomBarSelect = 0;
                    InitData();
                    return true;
                }else if (item.getItemId() == com.gag.accounting.R.id.action_item_project) {
                    bottomBarSelect = 1;
                    InitData();
                    return true;
                }

                return false;
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bottomBarSelect == 0){
                    DownloadMemberDues();
                }else if (bottomBarSelect == 1){
                    DownloadMemberPledge();
                }
            }
        });
    }

}