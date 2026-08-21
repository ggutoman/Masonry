package com.gag.accounting.Fragments.Project;

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

import com.gag.accounting.Adapter.Project.Adapter_Project_Member_Entry;

import org.gag.appdriver.App.Models.LodgeCalendarList;
import org.gag.appdriver.App.Models.ProjectDetail;
import org.gag.appdriver.Constants.PROJECT_CONSTANTS;
import org.gag.appdriver.Room.Entities.EProjectMaster;
import org.gag.appdriver.Utilities.LoadDialog;
import org.gag.appdriver.Utilities.Message_Dialog;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Project extends Fragment {

    private VM_Projects mViewModel;
    private Message_Dialog poMessage;
    private LoadDialog poLoad;
    private EProjectMaster loMaster;
    private List<ProjectDetail> laDetails = new ArrayList<>();
    private Adapter_Project_Member_Entry loProjectDetailAdapter;

    private String lsTransNox, lsLodgeIDxx, lsStackID, lsYearID;
    private int lnSelectStatus = -1, lnSelectDetail = -1, lnSelectType = -1;
    private ProjectDetail loSelectDetail;

    private ConstraintLayout layout_tools;
    private TextInputLayout til_proj_type, til_proj_status;
    private TextInputEditText tie_dtransact, tie_transaction_no, tie_name, tie_due, tie_remarks, tie_pledge_date, tie_pledge_amount;
    private MaterialAutoCompleteTextView auto_proj_type, auto_lodge_member, auto_proj_status;
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

        //allow status change, based on root navigation
        int count = requireActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {

            FragmentManager.BackStackEntry entry = requireActivity().getSupportFragmentManager().getBackStackEntryAt(count - 1);
            lsStackID = entry.getName() == null ? "" : entry.getName();
        }

        //initialze arguments
        lsYearID = "";
        lsLodgeIDxx = getArguments() == null || getArguments().getString("lodge_id") == null ? "" : getArguments().getString("lodge_id");
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
        tie_name = view.findViewById(R.id.tie_name);
        auto_proj_type = view.findViewById(R.id.auto_proj_type);
        tie_due = view.findViewById(R.id.tie_due);
        tie_remarks = view.findViewById(R.id.tie_remarks);
        mtv_totalpledge = view.findViewById(R.id.mtv_totalpledge);
        mtv_totalcontri = view.findViewById(R.id.mtv_totalcontri);
        mtv_totaldisb = view.findViewById(R.id.mtv_totaldisb);

        layout_tools = view.findViewById(R.id.layout_tools);
        til_proj_type = view.findViewById(R.id.til_proj_type);
        til_proj_status = view.findViewById(R.id.til_proj_status);
        btn_download = view.findViewById(R.id.btn_download);
        btn_view = view.findViewById(R.id.btn_view);
        auto_lodge_member = view.findViewById(R.id.auto_lodge_member);
        auto_proj_status = view.findViewById(R.id.auto_proj_status);
        tie_pledge_date = view.findViewById(R.id.tie_pledge_date);
        tie_pledge_amount = view.findViewById(R.id.tie_pledge_amount);
        btn_save = view.findViewById(R.id.btn_save);
        btn_add_detail = view.findViewById(R.id.btn_add_detail);
        btn_save_detail = view.findViewById(R.id.btn_save_detail);
    }

    private void AllowFields(boolean fisAllowed){
        tie_name.setEnabled(fisAllowed);
        til_proj_type.setEnabled(fisAllowed);
        tie_due.setEnabled(fisAllowed);
        tie_remarks.setEnabled(fisAllowed);
        btn_add_detail.setEnabled(fisAllowed);
        tie_pledge_date.setEnabled(fisAllowed);
        tie_pledge_amount.setEnabled(fisAllowed);
    }

    private void InitDataReceiver(){

        List<String> laStatus = new ArrayList<>();
        for (PROJECT_CONSTANTS proj_types : mViewModel.GetProjectStatus()) {
            laStatus.add(proj_types.getFsDescr());
        }

        List<String> laTypes = new ArrayList<>();
        for (PROJECT_CONSTANTS proj_types : mViewModel.GetProjectTypes()) {
            laTypes.add(proj_types.getFsDescr());
        }

        auto_proj_status.setAdapter(new ArrayAdapter<String>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                laStatus
        ));

        auto_proj_type.setAdapter(new ArrayAdapter<String>(
                requireActivity(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                laTypes
        ));

        //get current masonic year id
        mViewModel.GetLodgeCalendarList().observe(getViewLifecycleOwner(), new Observer<List<LodgeCalendarList>>() {
            @Override
            public void onChanged(List<LodgeCalendarList> lodgeCalendarLists) {

                if (lodgeCalendarLists == null){
                    Toast.makeText(requireActivity(), "Could not load masonic year info", Toast.LENGTH_SHORT).show();
                    AllowFields(false);
                    til_proj_status.setEnabled(false);
                    return;
                }

                String lsCurrentYear = mViewModel.GetFormattedDate(mViewModel.GetCurrentDate(), "yyyy");
                for (LodgeCalendarList lodgeYear : lodgeCalendarLists){
                    if (lodgeYear.getNYearxxxx().equalsIgnoreCase(lsCurrentYear)) lsYearID = lodgeYear.getSYearIDxx();
                }
                if (lsYearID.isEmpty()){
                    Toast.makeText(requireActivity(), "Could not load masonic year info", Toast.LENGTH_SHORT).show();
                    AllowFields(false);
                    til_proj_status.setEnabled(false);
                }
            }
        });

        //load master
        mViewModel.GetProject(lsTransNox).observe(getViewLifecycleOwner(), new Observer<EProjectMaster>() {
            @Override
            public void onChanged(EProjectMaster eProjectMaster) {

                if (eProjectMaster == null){

                    loMaster = new EProjectMaster(
                            lsTransNox,
                            "",
                            "0",
                            lsYearID,
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

                //allow button objects based on status
                boolean isOpen = Integer.parseInt(loMaster.getCTranStat()) < 2;

                //allow objects based on status (Complete / Cancelled)
                btn_add_detail.setEnabled(isOpen);
                btn_save.setVisibility(isOpen ? View.VISIBLE: View.GONE);

                //allow status change if record is for update or status is not open
                til_proj_status.setEnabled((lsStackID.equalsIgnoreCase("project_confirm") && isOpen));

                //enable fields
                AllowFields((!lsStackID.equalsIgnoreCase("project_confirm") && isOpen));

                lnSelectStatus = Integer.parseInt(loMaster.getCTranStat());
                lnSelectType = Integer.parseInt(loMaster.getCProjctTp());

                auto_proj_status.setText(mViewModel.GetProjectStatus().get(lnSelectStatus).getFsDescr(), false);
                auto_proj_type.setText(mViewModel.GetProjectTypes().get(lnSelectType).getFsDescr(), false);

                tie_transaction_no.setText(loMaster.getSProjctCd());
                tie_dtransact.setText(loMaster.getDTransact());
                tie_name.setText(loMaster.getSProjctNm());
                tie_due.setText(loMaster.getDDueDatex());
                tie_remarks.setText(loMaster.getSRemarksx());
                mtv_totaldisb.setText(loMaster.getNDisbTotl());
            }
        });

        //initialize existing details
        mViewModel.GetProjectDetails(lsTransNox).observe(getViewLifecycleOwner(), new Observer<List<ProjectDetail>>() {
            @Override
            public void onChanged(List<ProjectDetail> projectDetails) {

                if (projectDetails == null) return;

                mViewModel.ClearDetail();
                for (ProjectDetail loItem : projectDetails){

                    mViewModel.AddProjectDetail(
                            loItem.getSProjectCd(),
                            loItem.getSMemberID(),
                            loItem.getSMemberNme(),
                            loItem.getNEntryNox(),
                            loItem.getDPledgexx(),
                            loItem.getNPledgexx()
                    );
                }
            }
        });

        //load current details
        mViewModel.GetProjectList().observe(getViewLifecycleOwner(), new Observer<List<ProjectDetail>>() {
            @Override
            public void onChanged(List<ProjectDetail> eProjectDetails) {

                if (eProjectDetails == null) return;
                laDetails = eProjectDetails;

                loProjectDetailAdapter = new Adapter_Project_Member_Entry(requireActivity(), android.R.layout.simple_dropdown_item_1line, eProjectDetails);

                //load list
                auto_lodge_member.setAdapter(loProjectDetailAdapter);

                double ldbl_pledge = 0.00, ldbl_paid = 0.00;
                for (ProjectDetail loDetail : eProjectDetails){
                    ldbl_pledge += Double.parseDouble(loDetail.getNPledgexx());
                }
                lnSelectDetail = eProjectDetails.size() - 1;

                mtv_totalpledge.setText(String.valueOf(ldbl_pledge));
                mtv_totalcontri.setText(String.valueOf(ldbl_paid));
            }
        });
    }

    private void DownloadInfo(){

        mViewModel.DownloadProjectInformation(lsTransNox, new VM_Projects.OnDownload() {
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

        auto_proj_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectType = i;
                auto_proj_type.setText(mViewModel.GetProjectTypes().get(lnSelectType).getFsDescr(), false);
            }
        });

        auto_proj_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                lnSelectStatus = i;
                auto_proj_status.setText(mViewModel.GetProjectStatus().get(lnSelectStatus).getFsDescr(), false);
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
                lnSelectDetail = loProjectDetailAdapter.loProject.indexOf(loSelectDetail);

                //display details
                auto_lodge_member.setText(loSelectDetail.getSMemberNme());
                tie_pledge_date.setText(loSelectDetail.getDPledgexx());
                tie_pledge_amount.setText(loSelectDetail.getNPledgexx());
            }
        });

        tie_pledge_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar loCalendar = Calendar.getInstance();
                DatePickerDialog loValidPicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        String formatted = String.format("%04d-%02d-%02d", i, i1 + 1, i2);
                        tie_pledge_date.setText(formatted);

                        if (loSelectDetail == null) return;

                        if (!formatted.equals(loSelectDetail.getDPledgexx())){
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

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadInfo();
            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Project_Summary loSummary = new Fragment_Project_Summary();
                Bundle loBundle = new Bundle();

                loBundle.putString("year_id", lsTransNox);
                loBundle.putString("project_cd", lsTransNox);
                loSummary.setArguments(loBundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(org.gag.appdriver.R.id.frame_child, loSummary, "project_summary")
                        .addToBackStack("project_summary")
                        .commit();
            }
        });

        btn_add_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog_Project_Entry loEntry = new Dialog_Project_Entry(
                        requireActivity(),
                        lsLodgeIDxx,
                        mViewModel,
                        getViewLifecycleOwner(),
                        new Dialog_Project_Entry.OnSubmitEntry() {
                            @Override
                            public void OnSubmit(ProjectDetail loEntry) {

                                if (!mViewModel.AddProjectDetail(
                                        loEntry.getSProjectCd(),
                                        loEntry.getSMemberID(),
                                        loEntry.getSMemberNme(),
                                        "",
                                        loEntry.getDPledgexx(),
                                        loEntry.getNPledgexx()
                                    )
                                ){
                                    Toast.makeText(requireActivity(), "Pledge of the same member should not have the same date", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
                loEntry.Show();
            }
        });

        btn_save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loSelectDetail == null){
                    Toast.makeText(requireActivity(), "Please select a detail", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mViewModel.ReplaceProjectDetail(
                        lnSelectDetail,
                        loSelectDetail.getSMemberID(),
                        tie_pledge_date.getText() == null ? "1900-00-00" : tie_pledge_date.getText().toString(),
                        tie_pledge_amount.getText() == null ? "0.00" : tie_pledge_amount.getText().toString()
                )){
                    Toast.makeText(requireActivity(), "Pledge of the same member should not have the same date", Toast.LENGTH_SHORT).show();
                    return;
                }

                btn_save_detail.setVisibility(View.GONE);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check primary selections
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
                        loMaster.setSProjctNm(tie_name.getText() == null ? "" : tie_name.getText().toString());
                        loMaster.setCProjctTp(String.valueOf(lnSelectType));
                        loMaster.setDDueDatex(tie_due.getText() == null ? "1900-00-00" : tie_due.getText().toString());
                        loMaster.setSRemarksx(tie_remarks.getText() == null ? "" : tie_remarks.getText().toString());

                        mViewModel.SaveProject(loMaster, laDetails, String.valueOf(lnSelectStatus), new VM_Projects.OnTransaction() {
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
                                                .popBackStack(lsStackID, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

                                        //reload details
                                        if (!lsTransNox.isEmpty()) DownloadInfo();
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