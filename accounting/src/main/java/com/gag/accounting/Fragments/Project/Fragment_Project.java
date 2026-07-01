package com.gag.accounting.Fragments.Project;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gag.accounting.Dialog.Dialog_Project_Entry;
import com.gag.accounting.R;
import com.gag.accounting.ViewModel.VM_Projects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.gag.appdriver.App.Adapters.LodgeCalendarAdapter;
import org.gag.appdriver.App.Adapters.MemberAdapter;
import org.gag.appdriver.App.Adapters.ProjectMemberrAdapter;
import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Room.Entities.EMemberInfo;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Project extends Fragment {

    private VM_Projects mViewModel;
    private LodgeCalendarAdapter poCalAdapter;
    private LodgeCalendarList loSelectCalendar;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private EProjectMaster loMaster;
    private List<ProjectDetail> laDetails = new ArrayList<>();
    private ProjectMemberrAdapter loProjectDetailAdapter;

    private String lsLodgeIDx, lsTransNox;
    private int lnSelectStatus = -1, lnSelectDetail = -1, lnSelectType = -1;
    private ProjectDetail loSelectDetail;

    private ConstraintLayout layout_tools;
    private TextInputLayout til_lodge_cal, til_proj_type;
    private TextInputEditText tie_dtransact, tie_transaction_no, tie_name, tie_due, tie_remarks, tie_or_no, tie_pledge_date, tie_pledge_amount, tie_paid_amount;
    private MaterialAutoCompleteTextView auto_status, auto_proj_type, auto_lodge_member, auto_lodge_cal;
    private MaterialTextView btn_save_detail, btn_download, btn_view, mtv_totalpledge, mtv_totalcontri, mtv_totaldisb;
    private ImageButton btn_add_detail;
    private MaterialButton btn_save;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__project, container, false);

        mViewModel = new ViewModelProvider(requireActivity()).get(VM_Projects.class);
        poMessage = new Message_Dialog(requireActivity());
        poLoad = new LoadDialog(requireActivity());

        lsLodgeIDx = getArguments() == null || getArguments().getString("lodge_id") == null ? "" : getArguments().getString("lodge_id");
        lsTransNox = getArguments() == null || getArguments().getString("project_cd") == null ? "" : getArguments().getString("project_cd");

        poMessage.InitDialog();
        poLoad.InitDialog();

        InitViews(view);
        InitDataReceiver();
        InitListener();

        if (!lsTransNox.isEmpty()){
            DownloadInfo();
        }

        return view;
    }

    private void InitViews(View view){

        tie_transaction_no = view.findViewById(R.id.tie_transaction_no);
        tie_dtransact = view.findViewById(R.id.tie_dtransact);
        auto_status = view.findViewById(R.id.auto_status);
        tie_name = view.findViewById(R.id.tie_name);
        auto_proj_type = view.findViewById(R.id.auto_proj_type);
        tie_due = view.findViewById(R.id.tie_due);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        mtv_totalpledge = view.findViewById(R.id.mtv_totalpledge);
        mtv_totalcontri = view.findViewById(R.id.mtv_totalcontri);
        mtv_totaldisb = view.findViewById(R.id.mtv_totaldisb);

        layout_tools = view.findViewById(R.id.layout_tools);
        til_lodge_cal = view.findViewById(R.id.til_lodge_cal);
        til_proj_type = view.findViewById(R.id.til_proj_type);
        btn_download = view.findViewById(R.id.btn_download);
        btn_view = view.findViewById(R.id.btn_view);
        auto_lodge_cal = view.findViewById(R.id.auto_lodge_cal);
        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        tie_or_no = view.findViewById(R.id.tie_or_no);
        tie_pledge_date = view.findViewById(R.id.tie_pledge_date);
        tie_pledge_amount = view.findViewById(R.id.tie_pledge_amount);
        tie_paid_amount = view.findViewById(R.id.tie_paid_amount);
        btn_save = view.findViewById(R.id.btn_save);
        btn_add_detail = view.findViewById(R.id.btn_add_detail);
        btn_save_detail = view.findViewById(R.id.btn_save_detail);
    }

    private void AllowFields(boolean fisAllowed){

        til_lodge_cal.setEnabled(fisAllowed);
        tie_name.setEnabled(fisAllowed);
        til_proj_type.setEnabled(fisAllowed);
        tie_due.setEnabled(fisAllowed);
        tie_remarks.setEnabled(fisAllowed);
        btn_add_detail.setEnabled(fisAllowed);
        tie_pledge_date.setEnabled(fisAllowed);
        tie_pledge_amount.setEnabled(fisAllowed);
        tie_or_no.setEnabled(fisAllowed);
        tie_paid_amount.setEnabled(fisAllowed);
    }

    private void InitDataReceiver(){

        //check if selected lodge id has been initialized for the project
        if (lsLodgeIDx.isEmpty()){

            poMessage.ShowMessage(1, "Lodge ID for the project is not initialized", "Okay", "", new Message_Dialog.OnDialogClick() {
                @Override
                public void OnPositive(@NotNull AlertDialog poDialog) {

                    requireActivity().getSupportFragmentManager()
                            .popBackStack("project_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }

                @Override
                public void OnNegative(@NotNull AlertDialog poDialog) {}
            });
            return;
        }

        tie_dtransact.setText(mViewModel.GetCurrentDate());

        auto_status.setAdapter(new ArrayAdapter<String>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mViewModel.GetProjectStatus()
        ));

        auto_proj_type.setAdapter(new ArrayAdapter<String>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mViewModel.GetProjectTypes()
        ));

        //load master
        mViewModel.GetProject(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EProjectMaster>() {
            @Override
            public void onChanged(EProjectMaster eProjectMaster) {

                if (eProjectMaster == null){

                    loMaster = new EProjectMaster(
                            lsTransNox,
                            "",
                            "0",
                            lsLodgeIDx,
                            mViewModel.GetCurrentDate(),
                            "1900-00-00",
                            "",
                            "1900-00-00",
                            "0.00",
                            "0.00",
                            "0.00",
                            "0",
                            mViewModel.GetUserID() == null ? "" : mViewModel.GetUserID(),
                            mViewModel.GetCurrentDate(),
                            mViewModel.GetCurrentDateTime()
                    );
                    layout_tools.setVisibility(View.GONE);
                }else {
                    loMaster = eProjectMaster;
                    layout_tools.setVisibility(View.VISIBLE);
                }

                //enable selection
                AllowFields(!(loMaster.getCTranStat().equalsIgnoreCase("3") || loMaster.getCTranStat().equalsIgnoreCase("4")));

                lnSelectStatus = Integer.parseInt(loMaster.getCTranStat());
                lnSelectType = Integer.parseInt(loMaster.getCProjctTp());

                tie_transaction_no.setText(loMaster.getSProjctCd());
                tie_dtransact.setText(loMaster.getDTransact());
                auto_status.setText(mViewModel.GetProjectStatus().get(Integer.parseInt(loMaster.getCTranStat())), false);
                tie_name.setText(loMaster.getSProjctNm());
                auto_proj_type.setText(mViewModel.GetProjectTypes().get(Integer.parseInt(loMaster.getCProjctTp())), false);
                tie_due.setText(loMaster.getDDueDatex());
                tie_remarks.setText(loMaster.getSRemarksx());

                mViewModel.GetLodgeCalendars(lsLodgeIDx).observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                        if (lodgeCalendarLists == null) return;

                        poCalAdapter = new LodgeCalendarAdapter(requireActivity(),
                                org.gag.appdriver.R.layout.adapter_list_lodge_calendar,
                                lodgeCalendarLists);

                        auto_lodge_cal.setAdapter(poCalAdapter);

                        if (loMaster == null || loMaster.getSYearIDxx().isEmpty()) return;
                        for (LodgeCalendarList loItem : lodgeCalendarLists){

                            if (loItem.getSYearIDxx().equalsIgnoreCase(loMaster.getSYearIDxx())){

                                loSelectCalendar = loItem;
                                auto_lodge_cal.setText(loSelectCalendar.getSLodgeNme() + "(" + loSelectCalendar.getNYearxxxx() + ")", true);
                            }
                        }
                    }
                });
            }
        });

        //initialize existing details
        mViewModel.GetProjectDetails(lsTransNox).observe(getViewLifecycleOwner(), new Observer<List<ProjectDetail>>() {
            @Override
            public void onChanged(List<ProjectDetail> projectDetails) {

                if (projectDetails == null || projectDetails.size() < 1) return;

                mViewModel.ClearDetail();
                for (ProjectDetail loItem : projectDetails){

                    mViewModel.AddProjectDetail(
                            loItem.getSProjectCd(),
                            loItem.getSMemberID(),
                            loItem.getSMemberNme(),
                            loItem.getSORNoxxxx(),
                            loItem.getDPledgexx(),
                            loItem.getNPledgexx(),
                            loItem.getNAmtPaidx()
                    );
                }
            }
        });

        //load current details
        mViewModel.GetProjectList().observe(getViewLifecycleOwner(), new Observer<List<ProjectDetail>>() {
            @Override
            public void onChanged(List<ProjectDetail> eProjectDetails) {

                if (eProjectDetails == null || eProjectDetails.size() < 1) return;
                laDetails = eProjectDetails;

                loProjectDetailAdapter = new ProjectMemberrAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, eProjectDetails);

                //load list
                auto_lodge_member.setAdapter(loProjectDetailAdapter);

                double ldbl_pledge = 0.00, ldbl_paid = 0.00;
                for (ProjectDetail loDetail : eProjectDetails){

                    ldbl_pledge += Double.parseDouble(loDetail.getNPledgexx());
                    ldbl_paid += Double.parseDouble(loDetail.getNAmtPaidx());
                }
                lnSelectDetail = eProjectDetails.size() - 1;

                mtv_totalpledge.setText(String.valueOf(ldbl_pledge));
                mtv_totalcontri.setText(String.valueOf(ldbl_paid));
            }
        });
    }

    private void DownloadInfo(){

        mViewModel.DownloadProjectInformation(lsTransNox, "", new VM_Projects.OnDownload() {
            @Override
            public void OnLoad() {
                poLoad.ShowDialog("Downloading project information. Please wait . .");
            }

            @Override
            public void OnFinished(String fsMessage) {
                poLoad.DismissDialog();

                poMessage.ShowMessage(0, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                        InitDataReceiver();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {}
                });
            }
        });
    }

    private void InitListener(){

        auto_lodge_cal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (poCalAdapter == null) return;
                loSelectCalendar = poCalAdapter.getItem(i);

                auto_lodge_cal.setText(loSelectCalendar.getSLodgeNme() + "(" + loSelectCalendar.getNYearxxxx() + ")", false);
            }
        });

        auto_proj_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectType = i;
                auto_proj_type.setText(mViewModel.GetProjectTypes().get(lnSelectType), false);
            }
        });

        auto_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectStatus = i;
                auto_status.setText(mViewModel.GetProjectStatus().get(lnSelectStatus), false);
            }
        });

        tie_due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();
                DatePickerDialog loValidPicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        tie_due.setText(i + "-" + (i1 + 1) + "-" + i2);
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                );

                //Restrict minimum selection after the current day
                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "yyyy")),
                        Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "MM")),
                        Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "dd") + 1
                        )
                );

                loValidPicker.show();
            }
        });

        auto_lodge_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (loProjectDetailAdapter == null) return;

                loSelectDetail = loProjectDetailAdapter.getItem(i);
                lnSelectDetail = i;

                //display details
                auto_lodge_member.setText(loSelectDetail.getSMemberNme());
                tie_pledge_date.setText(loSelectDetail.getDPledgexx());
                tie_pledge_amount.setText(loSelectDetail.getNPledgexx());
                tie_or_no.setText(loSelectDetail.getSORNoxxxx());
                tie_paid_amount.setText(loSelectDetail.getNAmtPaidx());
            }
        });

        tie_pledge_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();
                DatePickerDialog loValidPicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        String ls_dPledge = i + "-" + (i1 + 1) + "-" + i2;
                        tie_pledge_date.setText(ls_dPledge);

                        if (loSelectDetail == null) return;

                        if (!ls_dPledge.equals(loSelectDetail.getDPledgexx())){
                            btn_save_detail.setVisibility(View.VISIBLE);
                        }else{
                            btn_save_detail.setVisibility(View.GONE);
                        }
                    }
                }, loCalendar.get(Calendar.YEAR),
                        loCalendar.get(Calendar.MONTH),
                        loCalendar.get(Calendar.DAY_OF_MONTH)
                );

                //Restrict minimum selection after the current day
                Calendar minDate = Calendar.getInstance();
                minDate.set(Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "yyyy")),
                        Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "MM")),
                        Integer.parseInt(mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "dd") + 1
                        )
                );

                loValidPicker.show();

            }
        });

        tie_pledge_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectDetail == null || charSequence.length() < 1){
                    btn_save_detail.setVisibility(View.GONE);
                }else if (Double.parseDouble(charSequence.toString()) == Double.parseDouble(loSelectDetail.getNPledgexx())){
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        tie_or_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectDetail == null) return;

                if (!charSequence.toString().equalsIgnoreCase(loSelectDetail.getSORNoxxxx())){
                    btn_save_detail.setVisibility(View.VISIBLE);
                }else{
                    btn_save_detail.setVisibility(View.GONE);
                }
            }
        });

        tie_paid_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {}

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loSelectDetail == null || charSequence.length() < 1){
                    btn_save_detail.setVisibility(View.GONE);
                }else if (Double.parseDouble(charSequence.toString()) == Double.parseDouble(loSelectDetail.getNAmtPaidx())){
                    btn_save_detail.setVisibility(View.GONE);
                }else{
                    btn_save_detail.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadInfo();
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_add_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog_Project_Entry loEntry = new Dialog_Project_Entry(
                        requireActivity(),
                        getArguments().getString("lodge_id"),
                        mViewModel,
                        getViewLifecycleOwner(),
                        new Dialog_Project_Entry.OnSubmitEntry() {
                            @Override
                            public void OnSubmit(ProjectDetail loEntry) {

                                mViewModel.AddProjectDetail(
                                        loEntry.getSProjectCd(),
                                        loEntry.getSMemberID(),
                                        loEntry.getSMemberNme(),
                                        loEntry.getSORNoxxxx(),
                                        loEntry.getDPledgexx(),
                                        loEntry.getNPledgexx(),
                                        loEntry.getNAmtPaidx()
                                );
                            }
                        }
                );
                loEntry.Show();
            }
        });

        btn_save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mViewModel.ReplaceProjectDetail(
                        lnSelectDetail,
                        tie_or_no.getText() == null ? "" : tie_or_no.getText().toString(),
                        tie_pledge_date.getText() == null ? "1900-00-00" : tie_pledge_date.getText().toString(),
                        tie_pledge_amount.getText() == null ? "" : tie_pledge_amount.getText().toString(),
                        tie_paid_amount.getText() == null ? "" : tie_paid_amount.getText().toString()
                );

                btn_save_detail.setVisibility(View.GONE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check primary selections
                if (loSelectCalendar == null){
                    Toast.makeText(requireActivity(), "Please select project year", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lnSelectStatus < 0){
                    Toast.makeText(requireActivity(), "Please select project status", Toast.LENGTH_SHORT).show();
                    return;
                }else if (lnSelectType < 0){
                    Toast.makeText(requireActivity(), "Please select project type", Toast.LENGTH_SHORT).show();
                    return;
                }

                poMessage.ShowMessage(2, "Is your information complete?", "No", "Yes", new Message_Dialog.OnDialogClick() {
                    @Override
                    public void OnPositive(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();
                    }

                    @Override
                    public void OnNegative(@NotNull AlertDialog poDialog) {
                        poDialog.dismiss();

                        //initialize parameters
                        loMaster.setSYearIDxx(loSelectCalendar.getSYearIDxx());
                        loMaster.setSProjctNm(tie_name.getText() == null ? "" : tie_name.getText().toString());
                        loMaster.setCProjctTp(String.valueOf(lnSelectType));
                        loMaster.setDDueDatex(tie_due.getText() == null ? "1900-00-00" : tie_due.getText().toString());
                        loMaster.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());
                        loMaster.setCTranStat(String.valueOf(lnSelectStatus));

                        mViewModel.SaveProject(loMaster, laDetails, new VM_Projects.OnTransaction() {
                            @Override
                            public void OnLoad() {
                                poLoad.ShowDialog("Saving project. Please wait . .");
                            }

                            @Override
                            public void OnSuccess() {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(0, "Project saved successfully", "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();

                                        requireActivity().getSupportFragmentManager()
                                                .popBackStack("project_entry", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void OnFailed(String fsMessage) {
                                poLoad.DismissDialog();

                                poMessage.ShowMessage(1, fsMessage, "Okay", "", new Message_Dialog.OnDialogClick() {
                                    @Override
                                    public void OnPositive(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }

                                    @Override
                                    public void OnNegative(@NotNull AlertDialog poDialog) {
                                        poDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

}