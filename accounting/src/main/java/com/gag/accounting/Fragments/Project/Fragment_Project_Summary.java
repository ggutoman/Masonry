package com.gag.accounting.Fragments.Project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gag.accounting.Adapter.Annual.Adapter_Annual_Members;
import com.gag.accounting.Adapter.Annual.Adapter_Annual_Summary;
import com.gag.accounting.Adapter.Project.Adapter_Pledge_Summary;
import com.gag.accounting.Adapter.Project.Adapter_Project_List;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Annual;
import com.gag.accounting.ViewModel.VM_Projects;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Fragments.Fragment_Child_Container;
import org.gag.appdriver.App.Models.AnnualMembers;
import org.gag.appdriver.App.Models.AnnualSummary;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Room.Entities.EAnnualMaster;
import org.gag.appdriver.Room.Entities.ELodgeCalendar;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Fragment_Project_Summary extends Fragment {

    private VM_Projects mViewModel;
    private LoadDialog poLoad;
    private Message_Dialog poMessage;

    private String lsYearIDxx, lsProjectCd, lsDfrom, lsDto;

    private MaterialTextView mtv_project_name, mtv_project_type, mtv_transaction, mtv_pledge_date, mtv_trans_stat, mtv_trans_total, mtv_collect_total;
    private ShapeableImageView btn_download;
    private ConstraintLayout layout_no_record;
    private MaterialCardView layout_pledges;
    private RecyclerView rcv_pledge_list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_project_summary, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Projects.class);
        poLoad = new LoadDialog(requireActivity());
        poMessage = new Message_Dialog(requireActivity());

        poLoad.InitDialog();
        poMessage.InitDialog();

        String lsCurrentYear = mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "yyyy");

        //initialize default range of date to display for the whole year
        lsDfrom = mViewModel.GetFormattedDate(lsCurrentYear + "-01-01", "yyyy-MM-dd");
        lsDto =  mViewModel.GetFormattedDate(lsCurrentYear + "-12-30", "yyyy-MM-dd");

        //initialize parameters for this transaction
        lsYearIDxx = (getArguments() == null || getArguments().getString("year_id") == null) ? "" : getArguments().getString("year_id");
        lsProjectCd = (getArguments() == null || getArguments().getString("project_cd") == null) ? "" : getArguments().getString("project_cd");

        DownloadInfo();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        return view;
    }

    private void InitViews(View view){

        mtv_project_name = view.findViewById(R.id.mtv_project_name);
        mtv_project_type = view.findViewById(R.id.mtv_project_type);
        mtv_transaction = view.findViewById(R.id.mtv_transaction);
        mtv_pledge_date = view.findViewById(R.id.mtv_pledge_date);
        mtv_trans_stat = view.findViewById(R.id.mtv_trans_stat);
        mtv_trans_total = view.findViewById(R.id.mtv_trans_total);
        mtv_collect_total = view.findViewById(R.id.mtv_collect_total);
        btn_download = view.findViewById(R.id.btn_download);
        layout_no_record = view.findViewById(R.id.layout_no_record);
        layout_pledges = view.findViewById(R.id.layout_pledges);
        rcv_pledge_list = view.findViewById(R.id.rcv_pledge_list);


    }

    private void DownloadInfo(){

        if (getArguments() == null) return;

        if (lsProjectCd.isEmpty()){

            //if project code is not set, download projects by year id if not empty
            if (!lsYearIDxx.isEmpty()){

                mViewModel.DownloadProjects(lsYearIDxx, lsDfrom, lsDto, new VM_Projects.OnTransaction() {
                    @Override
                    public void OnLoad() {
                        poLoad.ShowDialog("Downloading projects for the lodge year. Please wait . . .");
                    }

                    @Override
                    public void OnSuccess() {
                        poLoad.DismissDialog();

                        Toast.makeText(requireActivity(), "Successfully downloaded projects", Toast.LENGTH_SHORT).show();
                        InitDataReceiver();
                    }

                    @Override
                    public void OnFailed(String fsMessage) {
                        poLoad.DismissDialog();

                        poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                            @Override
                            public void OnPositive(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();

                                InitDataReceiver();
                            }

                            @Override
                            public void OnNegative(@NotNull AlertDialog poDialog) {
                                poDialog.dismiss();
                            }
                        });
                    }
                });
                return;
            }
            Toast.makeText(requireActivity(), "Year ID is not set", Toast.LENGTH_SHORT).show();

        }else{

            mViewModel.DownloadProjectInformation(lsProjectCd, new VM_Projects.OnDownload() {
                @Override
                public void OnLoad() {
                    poLoad.ShowDialog("Downloading project pledges. Please wait . .");
                }

                @Override
                public void OnFinished(String fsMessage) {
                    poLoad.DismissDialog();

                    Toast.makeText(requireActivity(), fsMessage, Toast.LENGTH_SHORT).show();
                    InitDataReceiver();
                }
            });
        }
    }

    private void InitDataReceiver(){

        if (lsProjectCd.isEmpty()){

            mViewModel.GetLodgeCalendarInfo(lsYearIDxx).observe(getViewLifecycleOwner(), new Observer<LodgeCalendarList>() {
                @Override
                public void onChanged(LodgeCalendarList eLodgeCalendar) {

                    if (eLodgeCalendar == null){

                        layout_no_record.setVisibility(View.VISIBLE);
                        layout_pledges.setVisibility(View.GONE);
                        return;
                    }

                    mtv_project_name.setText(eLodgeCalendar.getSLodgeNme());
                    mtv_project_type.setText("Year " + eLodgeCalendar.getNYearxxxx());
                    mtv_transaction.setText("Projects as of");
                    mtv_pledge_date.setText(mViewModel.GetFormattedDate(lsDto, "MMMM d, yyyy"));
                    mtv_trans_total.setText("0.00");
                    mtv_collect_total.setText("0.00");

                    //no need to display, summary of all projects
                    mtv_trans_stat.setVisibility(View.GONE);

                    mViewModel.GetProjectList(lsYearIDxx, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<EProjectMaster>>() {
                        @Override
                        public void onChanged(List<EProjectMaster> eProjectMasters) {

                            if (eProjectMasters == null || eProjectMasters.size() < 1){

                                layout_no_record.setVisibility(View.VISIBLE);
                                layout_pledges.setVisibility(View.GONE);
                                return;
                            }
                            layout_no_record.setVisibility(View.GONE);
                            layout_pledges.setVisibility(View.VISIBLE);

                            //display total from project list
                            double ldbl_trans_total = 0.00, ldbl_collect_total = 0.00;
                            for (EProjectMaster loItem : eProjectMasters){
                                ldbl_trans_total += Double.parseDouble(loItem.getNTranTotl());
                                ldbl_collect_total += Double.parseDouble(loItem.getNCollTotl());
                            }

                            mtv_trans_total.setText(String.valueOf(ldbl_trans_total));
                            mtv_collect_total.setText(String.valueOf(ldbl_collect_total));

                            //display and add viewing per projects
                            rcv_pledge_list.setAdapter(
                                    new Adapter_Project_List(requireActivity(), eProjectMasters, new Adapter_Project_List.OnSelectProject() {
                                        @Override
                                        public void OnSelect(EProjectMaster loProj) {

                                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();

                                            //show information of selected project
                                            Fragment_Project loProject = new Fragment_Project();
                                            Bundle loBundle = new Bundle();

                                            loBundle.putString("lodge_id", getArguments().getString("lodge_id"));
                                            loBundle.putString("project_cd", loProj.getSProjctCd());
                                            loProject.setArguments(loBundle);

                                            //add to child container, as bridge to initiate another fragment after call
                                            fragmentTransaction.replace(org.gag.appdriver.R.id.frame_child, new Fragment_Child_Container().newInstance("project_entry", loProject));
                                            fragmentTransaction.addToBackStack("project_entry");

                                            fragmentTransaction.commit();

                                        }
                                    }
                                )
                            );

                            rcv_pledge_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));

                        }
                    });
                }
            });

        }else {

            mViewModel.GetProject(lsProjectCd).observe(getViewLifecycleOwner(), new Observer<EProjectMaster>() {
                @Override
                public void onChanged(EProjectMaster eProjectMaster) {

                    if (eProjectMaster == null){

                        layout_no_record.setVisibility(View.VISIBLE);
                        layout_pledges.setVisibility(View.GONE);
                        return;
                    }
                    layout_no_record.setVisibility(View.GONE);
                    layout_pledges.setVisibility(View.VISIBLE);


                    mtv_project_name.setText(eProjectMaster.getSProjctNm());
                    mtv_project_type.setText(mViewModel.GetProjectTypes().get(Integer.parseInt(eProjectMaster.getCProjctTp())));
                    mtv_transaction.setText("Pledges as of");
                    mtv_pledge_date.setText(mViewModel.GetFormattedDate(lsDto, "MMMM d, yyyy"));
                    mtv_trans_total.setText("0.00");
                    mtv_collect_total.setText("0.00");

                    //no need to display, summary of all projects
                    mtv_trans_stat.setVisibility(View.VISIBLE);

                    switch (eProjectMaster.getCTranStat()){

                        case "0":
                            mtv_trans_stat.setText("Planning");
                            break;
                        case "1":
                            mtv_trans_stat.setText("On-Going");
                            break;
                        case "2":
                            mtv_trans_stat.setText("On-Hold");
                            break;
                        case "3":
                            mtv_trans_stat.setText("Completed");
                            break;
                        case "4":
                            mtv_trans_stat.setText("Cancelled");
                            break;
                    }

                    //display pledges
                    mViewModel.GetPledgeSummary(lsProjectCd, lsDfrom, lsDto).observe(getViewLifecycleOwner(), new Observer<List<ProjectDetail>>() {
                        @Override
                        public void onChanged(List<ProjectDetail> projectDetails) {

                            if (projectDetails == null || projectDetails.size() < 1){

                                layout_no_record.setVisibility(View.VISIBLE);
                                layout_pledges.setVisibility(View.GONE);
                                return;
                            }
                            layout_no_record.setVisibility(View.GONE);
                            layout_pledges.setVisibility(View.VISIBLE);

                            //display total from project list
                            double ldbl_trans_total = 0.00, ldbl_collect_total = 0.00;
                            for (ProjectDetail loItem : projectDetails){
                                ldbl_trans_total += Double.parseDouble(loItem.getNPledgexx());
                                ldbl_collect_total += Double.parseDouble(loItem.getNAmtPaidx());
                            }

                            mtv_trans_total.setText(String.valueOf(ldbl_trans_total));
                            mtv_collect_total.setText(String.valueOf(ldbl_collect_total));

                            rcv_pledge_list.setAdapter(new Adapter_Pledge_Summary(requireActivity(), projectDetails));
                            rcv_pledge_list.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                        }
                    });

                }
            });
        }
    }

    private void InitListener(){

        btn_download.setOnClickListener(view -> {

            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
            loBuilder.setTitleText("Select Date Range");

            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                @Override
                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                    poMessage.ShowMessage(2, "Do you want to download " + (lsProjectCd.isEmpty() ? "all projects within" : "all pledges within") + lsDfrom + " to " + lsDto + "?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                        @Override
                        public void OnPositive(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();
                        }

                        @Override
                        public void OnNegative(@NotNull AlertDialog poDialog) {
                            poDialog.dismiss();

                            //set date range parameters for downloading list
                            lsDfrom = mViewModel.GetFormatLongDate(selection.first);
                            lsDto = mViewModel.GetFormatLongDate(selection.second);

                            DownloadInfo();
                        }
                    });
                }
            });
            loPicker.show(getParentFragmentManager(), "DATE_RANGE_PICKER");

        });
    }
}